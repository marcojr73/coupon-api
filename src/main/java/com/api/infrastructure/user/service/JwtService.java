package com.api.infrastructure.user.service;

import com.api.application.user.port.TokenService;
import com.api.domain.user.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class JwtService implements TokenService {

    private static final String JWT_SECRET = "coupom-api-secret";
    private static final long JWT_EXPIRATION_SECONDS = 86400;

    public String generate(User user) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + JWT_EXPIRATION_SECONDS;
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
                "{\"sub\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"iat\":%d,\"exp\":%d}",
                escapeJson(user.getId()),
                escapeJson(user.getName()),
                escapeJson(user.getEmail()),
                issuedAt, expiresAt
        );
        String encodedHeader = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));
        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = sign(unsignedToken);
        return unsignedToken + "." + signature;
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    JWT_SECRET.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKey);
            return base64UrlEncode(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not generate JWT token", exception);
        }
    }

    private String base64UrlEncode(byte[] content) {
        return Base64.getUrlEncoder() .withoutPadding() .encodeToString(content);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\") .replace("\"", "\\\"");
    }
}
