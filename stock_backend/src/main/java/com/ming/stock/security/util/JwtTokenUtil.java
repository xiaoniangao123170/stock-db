package com.ming.stock.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ming
 * @Description: 认证过滤器 核心作用:认证用户信息 并且发放jwt票据
 */

public class JwtTokenUtil {
    // Token请求头
    public static final String TOKEN_HEADER = "authorization";
    // Token前缀，注意后面有空格 Bearer_
    public static final String TOKEN_PREFIX = "Bearer ";

    // 过期时间,单位毫秒 7天
    public static final long EXPIRITION = 1000 * 60 * 60 * 24 * 7;
    /**
     * ⚠HS256要求密钥最少32个字符！！
     * 生产环境不要硬编码：通过环境变量 JWT_SECRET 或 -DJWT_SECRET 注入。
     */
    private static final String APPSECRET_KEY = System.getProperty(
            "JWT_SECRET",
            System.getenv().getOrDefault(
                    "JWT_SECRET",
                    "change_me_to_a_32_char_secret_key"
            ));
    // 角色权限声明key
    private static final String ROLE_CLAIMS = "role";

    /**
     * 构建签名密钥 0.11.5必须SecretKey对象
     */
    private static SecretKey getSignSecretKey() {
        return Keys.hmacShaKeyFor(APPSECRET_KEY.getBytes());
    }

    /**
     * 生成Token
     * @param username 用户名
     * @param role 角色
     * @return jwt token
     */
    public static String createToken(String username, String role) {
        Map<String, Object> map = new HashMap<>();
        map.put(ROLE_CLAIMS, role);

        return Jwts.builder()
                .setSubject(username)
                .addClaims(map)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRITION))
                .signWith(getSignSecretKey())
                .compact();
    }

    /**
     * 校验Token，解析Claims；解析失败/篡改/过期 返回null
     */
    public static Claims checkJWT(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 过期、签名错误、格式错误都会进这里
            return null;
        }
    }

    /**
     * 从Token中获取用户名 subject
     */
    public static String getUsername(String token) {
        Claims claims = checkJWT(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户角色
     */
    public static String getUserRole(String token) {
        Claims claims = checkJWT(token);
        if (claims == null) {
            return null;
        }
        return claims.get(ROLE_CLAIMS, String.class);
    }

    /**
     * 校验Token是否过期
     * @return true 已过期；false未过期
     */
    public static boolean isExpiration(String token) {
        Claims claims = checkJWT(token);
        if (claims == null) {
            return true;
        }
        return claims.getExpiration().before(new Date());
    }

    /**
     * 去掉请求头 Bearer 前缀
     */
    public static String removeBearer(String headerToken) {
        if (headerToken != null && headerToken.startsWith(TOKEN_PREFIX)) {
            return headerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}