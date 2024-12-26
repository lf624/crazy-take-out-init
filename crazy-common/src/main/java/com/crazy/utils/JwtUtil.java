package com.crazy.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息，即自定义负载信息
     * @return String token
     */
    public static String createJwt(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成签名密钥
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        // 加密算法
        SecureDigestAlgorithm<SecretKey, SecretKey> algorithm = Jwts.SIG.HS256;

        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 基于最新的0.12版本
        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .signWith(key, algorithm)
                .expiration(exp);

        return builder.compact();
    }

    /**
     * 从token中解析出负载信息
     * @param secretKey jwt密钥
     * @param token 需要解析的token
     * @return Claims
     */
    public static Claims parseJwt(String secretKey, String token) {
        Jws<Claims> claims = Jwts.parser()
                // 设置签名的秘钥
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                // 设置需要解析的jwt
                .parseSignedClaims(token);
        return claims.getPayload();
    }
}
