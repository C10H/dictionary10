package com.dictionary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiduTranslateService {
    
    @Value("${baidu.translate.app-id}")
    private String appId;
    
    @Value("${baidu.translate.secret-key}")
    private String secretKey;
    
    @Value("${baidu.translate.api-url}")
    private String apiUrl;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String translate(String query, String from, String to) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = generateSign(query, salt);
        
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appId);
        params.put("salt", salt);
        params.put("sign", sign);
        
        String response = sendPostRequest(params);
        return parseTranslationResult(response);
    }
    
    private String generateSign(String query, String salt) {
        String str = appId + query + salt + secretKey;
        return DigestUtils.md5Hex(str);
    }
    
    private String sendPostRequest(Map<String, String> params) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (postData.length() > 0) {
                    postData.append("&");
                }
                postData.append(entry.getKey()).append("=").append(java.net.URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            
            StringEntity entity = new StringEntity(postData.toString(), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            }
        }
    }
    
    private String parseTranslationResult(String response) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(response);
        
        if (jsonNode.has("error_code")) {
            throw new Exception("Baidu API Error: " + jsonNode.get("error_msg").asText());
        }
        
        if (jsonNode.has("trans_result") && jsonNode.get("trans_result").isArray()) {
            JsonNode transResult = jsonNode.get("trans_result").get(0);
            return transResult.get("dst").asText();
        }
        
        throw new Exception("Invalid response format");
    }
}