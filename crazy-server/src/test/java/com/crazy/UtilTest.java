package com.crazy;

import com.crazy.constant.JwtClaimsConstant;
import com.crazy.utils.JwtUtil;
import com.crazy.utils.QcosUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class UtilTest {
    @Test
    public void testJwt() {
        String secretKey = null;
        try(InputStream input = ClassLoader.getSystemResourceAsStream("application.yml")) {
            if(input == null)
                throw new RuntimeException("input is null");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            secretKey = reader.lines()
                    .map(String::strip)
                    .filter(s -> s.startsWith("admin-secret-key:"))
                    .map(s -> s.substring(18))
                    .toList().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(secretKey == null)
            throw new RuntimeException("there is no secretKey properties");
        String token = JwtUtil.createJwt(secretKey, 20000, Map.of(JwtClaimsConstant.EMP_ID, 1));
        System.out.println(token);
        Map<String, Object> claim = JwtUtil.parseJwt(secretKey, token);
        System.out.println(claim); // {empId=1, exp=1735004604}
    }

    @Test
    public void testCOSUpload() throws IOException{
        URL configUrl = ClassLoader.getSystemResource("application-dev.yml");
        String region;
        String accessSecretId;
        String accessSecretKey;
        String bucketName;
        // 读取配置文件获得腾讯云相关配置
        try {
            List<String> allConfigs = Files.readAllLines(Path.of(configUrl.toURI()))
                    .stream()
                    .map(String::strip)
                    .toList();
            int index = allConfigs.indexOf("qcos:");
            region = allConfigs.get(index + 1).substring(8);
            accessSecretId = allConfigs.get(index + 2).substring(18);
            accessSecretKey = allConfigs.get(index + 3).substring(19);
            bucketName = allConfigs.get(index + 4).substring(13);
        }catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        // 测试上传文件
        byte[] data = Files.readAllBytes(Paths.get("../../BB1msDMN.jpeg"));
        QcosUtil qcosUtil = new QcosUtil(region, accessSecretId, accessSecretKey, bucketName);
        qcosUtil.upload(data, "test-BB1msDMN.jpeg");
    }
}
