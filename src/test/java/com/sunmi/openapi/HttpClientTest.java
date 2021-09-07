package com.sunmi.openapi;

import org.junit.jupiter.api.Test;

public class HttpClientTest {
    //Replace it with your own appId
    private static String appId = "your appId";
    //Replace it with your own appKey
    private static String appKey = "your appKey";
    //Replace it with your own appPrivateKey,for example, MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCg+cuxOnRilXQ/X8Tz6Neec6QMHlYi5+TFjRKiVFFoe7pWc79N1KsngWzngoWQDtaOgtosuWsz0SraiT/LzG3Lt+JwM8ruYQe+k6VVToK50BLaj1TU8eN05w9f68Crmzf/X/SZ504ByrZwYVh3v1mK9uMyBcvWIXvNahRk7VnWCfdaWqau0HJxPxvNbt/TkQNghOCLyXS/fvuupxhtQweDCaEsTcIjROkVIYpUACAZvgCAHlnhWqsir3whw/xLvyz+6nMCoV2p79yTpoNo/5ixw2m3vRsZmW8IktdHk6BADvqGZKE61ZsQmE+nUs8axz13BNNBrWAka+bI5V7GGTuNAgMBAAECggEAGqSdeQzivHpeDissru4H2frr2PGgchCNhcDup51rB+8KjUqFDD25wkUioEKzFn/ONTIlKCfcPWxOOnNi8rk4JvKdcNKxsLiwjnNjdvYSnux4YmI8uS375ppjqg2cCwsavpLEKkDlYViKz5jdCpzKFjz8p4fcD+nk0r/O9lFugAVNBn5l59eb2bHqNAanr8lX2Q9UWl2Tmi32AEcM9CbLQwJsgCAobSWY4qVvBRuLkWinaUckCZCg2kV3NCLS7chdKZIgyRPv8gFimkZt4YVo/gtOrZv2Doveb4BESzkEC45oXxceBVNENVVPJcN6WCG+b+K6WRzI2zhbRYZ7/bnkAQKBgQDRq/zuCIkWi6us1aYvxodxBSre8qTDj7MYagCtkAdJsvYUyCGnK4Zz5Dp0bbmQlFXr/mEJJweB2i2kxM5F/dSeOrvX4DdL/vbm9GcZIcWB3+PuKXjK8wS3b+cIj17PTxibTmIrT1JJZxGHAMgWTSOPJPqQGauDthit3TGnJ6ntAQKBgQDEi1UM4DUBMORpL+MIfz3tZmSFxV+NxHIhALOa3SUZLUi7prAdxOIePT0MWhiR5+lxAC49GUeQNaR576m3I8IJO8LuCjTND6WD/cTZ+6BE8vRaO6wACXWEX5xmoKs3TxKNoVROZsYxDhoK66F+iqhdNmxLm/SxQ75uGLcCW7eyjQKBgQDApf4n17/d70c17rhbfVZFCjJx1xB8fzRXYk5tKsEHfl+MiLYjG2LWaQUspQSzSxgHVhtihXHoZcc6dXnx5V/OYrjliSZFtWa2nil8dgM7abPLyF8sWk4jHbZxrwm2AT7itAqPMLqypvj7ykQN+aKKi7eNX4iv5EiYt3w/0bnYAQKBgQCHTtzollonTb+R4tPL/71keH9v0Ket87YWnmMHlIN7x78w4NqSQ/7fjo0+ua/8ksoVb2hly8eGskdfYu1WFZn5vVi/g+tB2Sm7qrMg1qhJj3FFxZruE5UB4sjLpoT4+MjvFEf0CbEcCHdJWVq+wTqToWoAy4czmk25ge5MA1R96QKBgQCJx2eyshek2DQn4i9hgUFqe3AngFyKD4lSpg+fZ0CQq2nija8Yk34ygMFkeq0BLIRc0oB8LrH4PjLNx8VKkcE/x426HViZR6HwiU4tGkXgShlerPu4+oe6kgmIHRgtURjNzHtrWpMa8GFxmjjTOdYOpf71U7dafMUCzNgiyzVuVQ==
    private static String appPrivateKey = "your appPrivateKey";
    //Replace it with your own sunmiPublicKey,for example,MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz2JsjPuNlPyBVJUO0IXP8nq4T0k9Dy6fQGngROBCyB7/dKJ/x3pVmk4tII7Pc2eCfDteRQDv1abvqbWmZ5Egp9E6Gsuq0a/7Jq+LwMDXE3MJLKPQ7dG8s+IPVO+TAQbsCA+wtMBy5iNiOFY6VJbSSXr/ABZO21Et1/Ab8ArDCwCyiG41bP90UiJ/93DtgfkhxaBp12w5IjuneW62bFsYAu5B6ZXFrGBnYI+Fm322BV26A8FO+fLDpd7z+m3qAgcEvrfSwcvtfbaLS8BdvtvJxjybGzobANK64qXXF0yxmzeMemLRP1nJWXMZh4G8V5+mRzr/ELp/QeA5Q/2Ow1NyawIDAQAB
    private static String sunmiPublicKey = "your sunmiPublicKey";
    HttpClient httpClient = new HttpClient(appId, appKey, appPrivateKey, sunmiPublicKey);

    @Test
    public void request() throws Exception {
        String params = "{\"request_id\":\"vKV97NVGNajuBhG865bc4aav220kj0617003fabf\",\"encrypt_factor\":\"abc12345\"}";
        String url = "https://openapi.sunmi.com/v2/eid/eid/idcard/decode";
        String res = httpClient.request(url, params, "hmac");
        System.out.println(res);
    }

    @Test
    public void uploadFile() throws Exception {
        String params = "{\"md5\":\"cfd59674e7c36a4d33d47080740c52c1\",\"file_type_key\":\"appstore_apk\"}";
        String url = "https://openapi.sunmi.com/v2/midplat/filecore/file/uploadApk";
        String filePath = "/your/path/xxx.apk";
        String res = httpClient.uploadFile(url, filePath, params, "hmac");
        System.out.println(res);
    }
}
