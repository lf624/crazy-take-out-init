package com.crazy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
public class HttpClientUtil {

    public static String doGet(String url, Map<String, String> query) {
        String result = "";

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            if(query != null && !query.isEmpty())
                query.forEach(uriBuilder::addParameter);
            // 创建 GET 请求
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            // 发送请求，接收响应
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
            log.debug("doGet response: {}", result);
        } catch (IOException | URISyntaxException e) {
            log.error("http doGet Exception: {}", e.toString());
        }
        return result;
    }
}
