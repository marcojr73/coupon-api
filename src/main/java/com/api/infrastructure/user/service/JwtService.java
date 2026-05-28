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

    public String generate(User user) {
        long issuedAt = System.currentTimeMillis() / 1000;
        long expiresAt = issuedAt + (24 * 60 * 60);
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

    public String extractSubject(String token) {
        try {
            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            return extractField(payloadJson, "sub");

        } catch (Exception e) {
            throw new IllegalStateException("Could not extract subject from token", e);
        }
    }

    private String extractField(String json, String field) {
        String patternString = "\"" + field + "\":\"";
        String patternNumber = "\"" + field + "\":";

        int start = json.indexOf(patternString);

        if (start != -1) {
            start += patternString.length();
            int end = json.indexOf("\"", start);
            if (end == -1) return null;
            return json.substring(start, end);
        }

        start = json.indexOf(patternNumber);
        if (start == -1) return null;

        start += patternNumber.length();

        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        if (end == -1) return null;

        return json.substring(start, end).trim();
    }

    public boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                return false;
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String signature = parts[2];

            String expectedSignature = sign(unsignedToken);
            if (!expectedSignature.equals(signature)) {
                return false;
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            String expValue = extractField(payloadJson, "exp");
            if (expValue == null) return false;

            long exp = Long.parseLong(expValue);
            long now = Instant.now().getEpochSecond();

            return now < exp;

        } catch (Exception e) {
            return false;
        }
    }
}
