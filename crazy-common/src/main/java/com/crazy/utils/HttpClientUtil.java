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

    // TODO 修改完善
    public static String doGet(String url, Map<String, String> query) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            query.forEach(uriBuilder::addParameter);
            HttpGet httpGet = new HttpGet(uriBuilder.build());

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            log.debug("doGet response: {}", json);
            return json;
        }catch (IOException | URISyntaxException e) {
            log.error("http doGet Exception: {}", e.toString());
        }
        return null;
    }
}
