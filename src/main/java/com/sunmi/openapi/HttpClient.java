package com.sunmi.openapi;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

public class HttpClient {
    private final String appId;
    private final String appKey;
    private final String appPrivateKey;
    private final String sunmiPublicKey;

    public HttpClient(String appId, String appkey, String appPrivateKey, String sunmiPublicKey) {
        this.appId = appId;
        this.appKey = appkey;
        this.appPrivateKey = appPrivateKey;
        this.sunmiPublicKey = sunmiPublicKey;
    }

    /**
     * Http request
     * @param url: api url
     * @param params: json string parameters
     * @param signType: RSA|hmac
     * @return response body
     * @throws Exception
     */
    public String request(String url, String params, String signType) throws Exception {
        String respBody = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity s = new StringEntity(params, "utf-8");
        httpPost.setEntity(s);
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String nonce = UUID.randomUUID().toString();
        String sign;
        if(signType.equals("RSA")){
            sign = generateRsa2048Sign(appId, appPrivateKey,timestamp, nonce, params);
        }else {
            sign = generateHmac256Sign(appId, appKey, timestamp, nonce, params);
        }
        httpPost.setHeader("Sunmi-Sign", sign);
        httpPost.setHeader("Sunmi-Timestamp", timestamp);
        httpPost.setHeader("Sunmi-Nonce", nonce);
        httpPost.setHeader("Sunmi-Appid", appId);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if(response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Server error");
        }
        HttpEntity entity = response.getEntity();
        if(entity != null) {
            respBody = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);
        response.close();
        JSONObject jsonObject = new JSONObject(respBody);
        Integer respCode = jsonObject.getInt("code");
        if(!respCode.equals(30001) && !respCode.equals(30000)){
            String respSign = response.getHeaders("Sunmi-Sign")[0].getValue();
            String respTimestamp = response.getHeaders("Sunmi-Timestamp")[0].getValue();
            String respNonce = response.getHeaders("Sunmi-Nonce")[0].getValue();
            boolean result;
            //verify sign
            if(signType.equals("RSA")) {
                result = verifyRsa2048Sign(appId, sunmiPublicKey, respSign, respTimestamp, respNonce, respBody);
            }else {
                result = generateHmac256Sign(appId, appKey, respTimestamp, respNonce, respBody).equals(respSign);
            }
            if(!result){
                //throw exception
                throw new Exception("Response signature error");
            }
        }
        return respBody;
    }

    /**
     * Upload files
     * @param url: api url
     * @param filePath: file path
     * @param params: json string parameters
     * @param signType: RSA|hmac
     * @return response body
     * @throws Exception
     */
    public String uploadFile(String url, String filePath, String params, String signType) throws Exception {
        String respBody = "";
        File file = new File(filePath);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // build httpentity object and assign the file that need to be uploaded
        HttpEntity postData = MultipartEntityBuilder.create().addBinaryBody("file", file).addTextBody("params", params).build();
        // build http request and assign httpentity object to it that we build above
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(postData);
        //set header
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String nonce = UUID.randomUUID().toString();
        String sign;
        if(signType.equals("RSA")){
            sign = generateRsa2048Sign(appId, appPrivateKey, timestamp, nonce, params);
        }else {
            sign = generateHmac256Sign(appId, appKey, timestamp, nonce, params);
        }
        httpPost.setHeader("Sunmi-Sign", sign);
        httpPost.setHeader("Sunmi-Timestamp", timestamp);
        httpPost.setHeader("Sunmi-Nonce", nonce);
        httpPost.setHeader("Sunmi-Appid", appId);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if(response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Response signature error");
        }
        HttpEntity entity = response.getEntity();
        if(entity != null) {
            respBody = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);
        response.close();
        JSONObject jsonObject = new JSONObject(respBody);
        Integer respCode = jsonObject.getInt("code");
        if(!respCode.equals(30001) && !respCode.equals(30000)){
            String respSign = response.getHeaders("Sunmi-Sign")[0].getValue();
            String respTimestamp = response.getHeaders("Sunmi-Timestamp")[0].getValue();
            String respNonce = response.getHeaders("Sunmi-Nonce")[0].getValue();
            boolean result;
            //verify sign
            if(signType.equals("RSA")) {
                result = verifyRsa2048Sign(appId, sunmiPublicKey, respSign, respTimestamp, respNonce, respBody);
            }else {
                result = generateHmac256Sign(appId, appKey, respTimestamp, respNonce, respBody).equals(respSign);
            }
            if(!result){
                throw new Exception("Response signature error");
            }
        }
        return respBody;
    }

    /**
     * Generate Hmac-sha256 signature
     * @param appId: appId
     * @param appKey: appKey
     * @param timestamp: timestamp
     * @param nonce: random string
     * @param params: json string parameters
     * @return signature
     * @throws Exception
     */
    public String generateHmac256Sign(String appId, String appKey, String timestamp, String nonce, String params) throws Exception {
        String content = params + appId + timestamp + nonce;
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(appKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(key);
        byte[] array = hmacSHA256.doFinal(content.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    /**
     * Generate Rsa 2048 signature
     * @param appId
     * @param privateKey: rsa private key
     * @param timestamp
     * @param nonce
     * @param params
     * @return signature
     * @throws Exception
     */
    public String generateRsa2048Sign(String appId, String privateKey, String timestamp, String nonce, String params) throws Exception {
        String content = params + appId + timestamp + nonce;
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(priKey);
        signature.update(content.getBytes());
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * Verify signature with public key
     * @param appId
     * @param publicKey: rsa public key
     * @param sign
     * @param timestamp
     * @param nonce: random string
     * @param params
     * @return
     */
    public boolean verifyRsa2048Sign(String appId, String publicKey, String sign, String timestamp, String nonce, String params) {
        try {
            String content = params + appId + timestamp + nonce;
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey =  keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("Sha256WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            return false;
        }
    }
}
