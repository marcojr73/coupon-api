package com.api.infrastructure.user.service;

import com.api.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String USER_ID = "user-id-123";
    private static final String NAME = "Luke Skywalker";
    private static final String EMAIL = "luke@starwars.com";

    private final JwtService jwtService = new JwtService();

    @Test
    @DisplayName("Deve gerar token JWT com três partes")
    void shouldGenerateJwtTokenWithThreeParts() {
        User user = user();

        String token = jwtService.generate(user);

        String[] parts = token.split("\\.");

        assertThat(parts).hasSize(3);
        assertThat(parts[0]).isNotBlank();
        assertThat(parts[1]).isNotBlank();
        assertThat(parts[2]).isNotBlank();
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void shouldGenerateValidJwtToken() {
        User user = user();

        String token = jwtService.generate(user);

        boolean result = jwtService.isValid(token);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve extrair subject do token JWT")
    void shouldExtractSubjectFromJwtToken() {
        User user = user();

        String token = jwtService.generate(user);

        String subject = jwtService.extractSubject(token);

        assertThat(subject).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("Deve gerar token com dados do usuário no payload")
    void shouldGenerateTokenWithUserDataInPayload() {
        User user = user();

        String token = jwtService.generate(user);

        String payloadJson = decodePayload(token);

        assertThat(payloadJson).contains("\"sub\":\"" + USER_ID + "\"");
        assertThat(payloadJson).contains("\"name\":\"" + NAME + "\"");
        assertThat(payloadJson).contains("\"email\":\"" + EMAIL + "\"");
        assertThat(payloadJson).contains("\"iat\":");
        assertThat(payloadJson).contains("\"exp\":");
    }

    @Test
    @DisplayName("Deve gerar token com expiração de 24 horas")
    void shouldGenerateTokenWithExpirationOfTwentyFourHours() {
        User user = user();

        long beforeGeneration = Instant.now().getEpochSecond();

        String token = jwtService.generate(user);

        long afterGeneration = Instant.now().getEpochSecond();

        String payloadJson = decodePayload(token);

        long issuedAt = extractLongField(payloadJson, "iat");
        long expiresAt = extractLongField(payloadJson, "exp");

        assertThat(issuedAt).isBetween(beforeGeneration, afterGeneration);
        assertThat(expiresAt).isEqualTo(issuedAt + 24 * 60 * 60);
    }

    @Test
    @DisplayName("Deve escapar aspas e barras invertidas no payload")
    void shouldEscapeQuotesAndBackslashesInPayload() {
        User user = User.builder()
                .id("user\\id")
                .name("Luke \"Jedi\"")
                .email("luke\\jedi@starwars.com")
                .build();

        String token = jwtService.generate(user);

        String payloadJson = decodePayload(token);

        assertThat(payloadJson).contains("\"sub\":\"user\\\\id\"");
        assertThat(payloadJson).contains("\"name\":\"Luke \\\"Jedi\\\"\"");
        assertThat(payloadJson).contains("\"email\":\"luke\\\\jedi@starwars.com\"");
    }

    @Test
    @DisplayName("Deve usar string vazia quando dados do usuário forem nulos")
    void shouldUseEmptyStringWhenUserDataIsNull() {
        User user = User.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        String token = jwtService.generate(user);

        String payloadJson = decodePayload(token);

        assertThat(payloadJson).contains("\"sub\":\"\"");
        assertThat(payloadJson).contains("\"name\":\"\"");
        assertThat(payloadJson).contains("\"email\":\"\"");
    }

    @Test
    @DisplayName("Deve retornar false quando token tiver formato inválido")
    void shouldReturnFalseWhenTokenFormatIsInvalid() {
        boolean result = jwtService.isValid("invalid-token");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao extrair subject de token com formato inválido")
    void shouldThrowExceptionWhenExtractingSubjectFromInvalidTokenFormat() {
        assertThatThrownBy(() -> jwtService.extractSubject("invalid-token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Could not extract subject from token");
    }

    @Test
    @DisplayName("Deve retornar false quando assinatura do token for inválida")
    void shouldReturnFalseWhenTokenSignatureIsInvalid() {
        User user = user();

        String token = jwtService.generate(user);

        String invalidToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalid-signature";

        boolean result = jwtService.isValid(invalidToken);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando payload não puder ser decodificado")
    void shouldReturnFalseWhenPayloadCannotBeDecoded() {
        User user = user();

        String token = jwtService.generate(user);
        String[] parts = token.split("\\.");

        String invalidToken = parts[0] + ".invalid-payload." + parts[2];

        boolean result = jwtService.isValid(invalidToken);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando token estiver expirado")
    void shouldReturnFalseWhenTokenIsExpired() {
        long issuedAt = Instant.now().getEpochSecond() - 100;
        long expiresAt = Instant.now().getEpochSecond() - 10;

        String token = createToken(
                "{\"alg\":\"HS256\",\"typ\":\"JWT\"}",
                """
                {
                    "sub": "%s",
                    "name": "%s",
                    "email": "%s",
                    "iat": %d,
                    "exp": %d
                }
                """.formatted(USER_ID, NAME, EMAIL, issuedAt, expiresAt)
        );

        boolean result = jwtService.isValid(token);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando token não possuir expiração")
    void shouldReturnFalseWhenTokenDoesNotHaveExpiration() {
        String token = createToken(
                "{\"alg\":\"HS256\",\"typ\":\"JWT\"}",
                """
                {
                    "sub": "%s",
                    "name": "%s",
                    "email": "%s",
                    "iat": %d
                }
                """.formatted(USER_ID, NAME, EMAIL, Instant.now().getEpochSecond())
        );

        boolean result = jwtService.isValid(token);

        assertThat(result).isFalse();
    }

    private User user() {
        return User.builder()
                .id(USER_ID)
                .name(NAME)
                .email(EMAIL)
                .build();
    }

    private String decodePayload(String token) {
        String[] parts = token.split("\\.");

        return new String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
        );
    }

    private long extractLongField(String json, String field) {
        String pattern = "\"" + field + "\":";

        int start = json.indexOf(pattern);

        if (start == -1) {
            throw new IllegalArgumentException("Field not found: " + field);
        }

        start += pattern.length();

        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return Long.parseLong(json.substring(start, end).trim());
    }

    private String createToken(String headerJson, String payloadJson) {
        String encodedHeader = base64UrlEncode(headerJson);
        String encodedPayload = base64UrlEncode(payloadJson);
        String unsignedToken = encodedHeader + "." + encodedPayload;
        String signature = sign(unsignedToken);

        return unsignedToken + "." + signature;
    }

    private String sign(String content) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey =
                    new javax.crypto.spec.SecretKeySpec(
                            "coupom-api-secret".getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            mac.init(secretKey);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String base64UrlEncode(String content) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }
}