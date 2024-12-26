package com.crazy;

import com.crazy.constant.JwtClaimsConstant;
import com.crazy.utils.JwtUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
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
}
