package com.crazy.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Slf4j
@Data
@AllArgsConstructor
public class QcosUtil {
    private String region;
    private String accessSecretId;
    private String accessSecretKey;
    private String bucketName;

    public String upload(byte[] bytes, String objectName) {
        COSClient cosClient = createCOSClient();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 设置上传的流准确的流长度
        objectMetadata.setContentLength(bytes.length);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName,
                new ByteArrayInputStream(bytes), objectMetadata);

        try {
            PutObjectResult result = cosClient.putObject(putObjectRequest);
        } catch (CosServiceException se) {
            System.out.println("Caught a CosServiceException, which means the interaction completed normally, " +
                    "but the operation failed.");
            System.out.println("Error Message: " + se.getErrorMessage());
            System.out.println("Error Code: " + se.getErrorCode());
            System.out.println("Request Id: " + se.getRequestId());
            System.out.println("Trace Id: " + se.getTraceId());
            System.out.println("more info can see: https://cloud.tencent.com/document/product/436/35218");
        } catch (CosClientException ce) {
            System.out.println("Caught a CosClientException, which is caused by the failure of " +
                    "the client to complete normal interaction with the server");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            cosClient.shutdown();
        }

        // 文件访问路径：https://BucketName.region.myqcloud.com/ObjectName
        StringBuilder sb = new StringBuilder("https://");
        sb.append(bucketName).append(".")
                .append(Region.formatRegion(region))
                .append(".myqcloud.com")
                .append("/")
                .append(objectName);
        log.info("File upload to {}", sb);
        return sb.toString();
    }

    private COSClient createCOSClient() {
        // 设置用户身份信息
        COSCredentials cred = new BasicCOSCredentials(accessSecretId, accessSecretKey);
        // 后续请求 COS 的客户端设置
        ClientConfig clientConfig = new ClientConfig();
        // 设置 bucket 的地域
        clientConfig.setRegion(new Region(region));
        // 可选设置
        // 设置 socket 读取超时，默认 30s
        // clientConfig.setSocketTimeout(30*1000);
        // 设置建立连接超时，默认 30s
        // clientConfig.setConnectionTimeout(30*1000);

        // 关闭打印shutdown堆栈
        clientConfig.setPrintShutdownStackTrace(false);

        // 如果需要的话，设置 http 代理，ip 以及 port
        // clientConfig.setHttpProxyIp("httpProxyIp");
        // clientConfig.setHttpProxyPort(80);
        log.info("create COSClient: {}", Region.formatRegion(region));
        // 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }
}
