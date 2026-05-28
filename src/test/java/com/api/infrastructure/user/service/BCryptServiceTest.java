package com.api.infrastructure.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BCryptServiceTest {

    private static final String RAW_PASSWORD = "#JEDIKNIGHT!";
    private static final String ENCODED_PASSWORD = "$encoded-#JEDIKNIGHT!";

    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final BCryptService bCryptService = new BCryptService(passwordEncoder);

    @Test
    @DisplayName("Deve codificar texto usando PasswordEncoder")
    void shouldEncodeTextUsingPasswordEncoder() {
        when(passwordEncoder.encode(RAW_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        String result = bCryptService.encode(RAW_PASSWORD);

        assertThat(result).isEqualTo(ENCODED_PASSWORD);

        verify(passwordEncoder).encode(RAW_PASSWORD);
    }

    @Test
    @DisplayName("Deve retornar true quando os textos corresponderem")
    void shouldReturnTrueWhenTextsMatch() {
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD))
                .thenReturn(true);

        boolean result = bCryptService.matches(RAW_PASSWORD, ENCODED_PASSWORD);

        assertThat(result).isTrue();

        verify(passwordEncoder).matches(RAW_PASSWORD, ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("Deve retornar false quando os textos não corresponderem")
    void shouldReturnFalseWhenTextsDoNotMatch() {
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD))
                .thenReturn(false);

        boolean result = bCryptService.matches(RAW_PASSWORD, ENCODED_PASSWORD);

        assertThat(result).isFalse();

        verify(passwordEncoder).matches(RAW_PASSWORD, ENCODED_PASSWORD);
    }
}
