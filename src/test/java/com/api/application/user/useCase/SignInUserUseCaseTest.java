package com.api.application.user.useCase;

import com.api.application.user.dto.SignInUserUseCaseInput;
import com.api.application.user.port.CryptoService;
import com.api.application.user.port.TokenService;
import com.api.core.exception.RestException;
import com.api.domain.user.entity.User;
import com.api.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignInUserUseCaseTest {

    private static final String USER_ID = "user-id-123";
    private static final String NAME = "Luke Skywalker";
    private static final String EMAIL = "luke@starwars.com";
    private static final String PASSWORD = "123456";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded-password";
    private static final String TOKEN = "jwt-token";

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CryptoService cryptoService = mock(CryptoService.class);
    private final TokenService tokenService = mock(TokenService.class);

    private final SignInUserUseCase signInUserUseCase =
            new SignInUserUseCase(userRepository, cryptoService, tokenService);

    @Test
    @DisplayName("Deve autenticar usuário e retornar token")
    void shouldAuthenticateUserAndReturnToken() {
        User user = user();

        SignInUserUseCaseInput input = SignInUserUseCaseInput.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(cryptoService.matches(PASSWORD, ENCODED_PASSWORD))
                .thenReturn(true);

        when(tokenService.generate(user))
                .thenReturn(TOKEN);

        String result = signInUserUseCase.execute(input);

        assertThat(result).isEqualTo(TOKEN);

        verify(userRepository).findByEmail(EMAIL);
        verify(cryptoService).matches(PASSWORD, ENCODED_PASSWORD);
        verify(tokenService).generate(user);
    }

    @Test
    @DisplayName("Deve lançar exceção quando e-mail não existir")
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        SignInUserUseCaseInput input = SignInUserUseCaseInput.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> signInUserUseCase.execute(input))
                .isInstanceOf(RestException.class)
                .hasMessage("Email or password is incorrect");

        verify(userRepository).findByEmail(EMAIL);
        verify(cryptoService, never()).matches(PASSWORD, ENCODED_PASSWORD);
        verify(tokenService, never()).generate(user());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha for inválida")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        User user = user();

        SignInUserUseCaseInput input = SignInUserUseCaseInput.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(cryptoService.matches(PASSWORD, ENCODED_PASSWORD))
                .thenReturn(false);

        assertThatThrownBy(() -> signInUserUseCase.execute(input))
                .isInstanceOf(RestException.class)
                .hasMessage("Email or password is incorrect");

        verify(userRepository).findByEmail(EMAIL);
        verify(cryptoService).matches(PASSWORD, ENCODED_PASSWORD);
        verify(tokenService, never()).generate(user);
    }

    private User user() {
        return User.builder()
                .id(USER_ID)
                .name(NAME)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .build();
    }
}