package com.api.application.user.useCase;

import com.api.application.user.dto.SignUpUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseOutput;
import com.api.application.user.port.CryptoService;
import com.api.core.exception.RestException;
import com.api.domain.user.entity.User;
import com.api.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private SignUpUserUseCase signUpUserUseCase;

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso")
    void shouldSignUpUserWithSuccess() {
        SignUpUserUseCaseInput input = SignUpUserUseCaseInput.builder()
                .name("Luke Skywalker")
                .email("luke@starwars.com")
                .password("force123")
                .build();

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());
        when(cryptoService.encode(input.getPassword())).thenReturn("encrypted_password");
        
        User savedUser = User.builder()
                .id("user-id")
                .name(input.getName())
                .email(input.getEmail())
                .password("encrypted_password")
                .build();
        
        when(userRepository.create(any(User.class))).thenReturn(savedUser);

        SignUpUserUseCaseOutput output = signUpUserUseCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isEqualTo("user-id");
        assertThat(output.getName()).isEqualTo(input.getName());
        assertThat(output.getEmail()).isEqualTo(input.getEmail());

        verify(userRepository).findByEmail(input.getEmail());
        verify(cryptoService).encode(input.getPassword());
        verify(userRepository).create(argThat(user -> 
            user.getName().equals(input.getName()) &&
            user.getEmail().equals(input.getEmail()) &&
            user.getPassword().equals("encrypted_password")
        ));
    }

    @Test
    @DisplayName("Deve lançar erro quando o email já estiver em uso")
    void shouldThrowErrorWhenEmailIsAlreadyInUse() {
        SignUpUserUseCaseInput input = SignUpUserUseCaseInput.builder()
                .name("Luke Skywalker")
                .email("luke@starwars.com")
                .password("force123")
                .build();

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> signUpUserUseCase.execute(input))
                .isInstanceOf(RestException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT)
                .hasMessage("Email already in use");

        verify(userRepository).findByEmail(input.getEmail());
        verifyNoMoreInteractions(cryptoService, userRepository);
    }
}
