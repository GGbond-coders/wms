package com.wms.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.wms.pojo.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    private static final String SECRET = "wms-secret-key-2024"; // 实际项目中应该从配置文件读取
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时

    /**
     * 生成JWT token
     */
    public static String generateToken(User user) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        return JWT.create()
                .withHeader(map)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole())
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证token
     */
    public static boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 从token中获取用户信息
     */
    public static User getUserFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            DecodedJWT jwt = verifier.verify(token);

            User user = new User();
            user.setId(jwt.getClaim("id").asLong());
            user.setUsername(jwt.getClaim("username").asString());
            user.setRole(jwt.getClaim("role").asString());

            return user;
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}