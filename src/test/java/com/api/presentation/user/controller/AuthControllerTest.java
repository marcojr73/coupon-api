package com.api.presentation.user.controller;

import com.api.application.user.dto.SignInUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseOutput;
import com.api.application.user.useCase.SignInUserUseCase;
import com.api.application.user.useCase.SignUpUserUseCase;
import com.api.infrastructure.user.dao.UserDao;
import com.api.infrastructure.user.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private static final String USER_ID = "user-id-123";
    private static final String NAME = "Luke Skywalker";
    private static final String EMAIL = "luke@starwars.com";
    private static final String PASSWORD = "123456";
    private static final String INVALID_NAME = "Lu";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String INVALID_PASSWORD = "123";
    private static final String JWT_TOKEN = "jwt-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignUpUserUseCase signUpUserUseCase;

    @MockitoBean
    private SignInUserUseCase signInUserUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDao userDao;

    @Test
    @DisplayName("Deve cadastrar usuário e retornar status 201")
    void shouldSignUpUserAndReturnCreated() throws Exception {
        SignUpUserUseCaseOutput output = SignUpUserUseCaseOutput.builder()
                .id(USER_ID)
                .name(NAME)
                .email(EMAIL)
                .build();

        when(signUpUserUseCase.execute(any(SignUpUserUseCaseInput.class)))
                .thenReturn(output);

        String requestBody = signUpRequest(NAME, EMAIL, PASSWORD);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.message").value("Created"));

        ArgumentCaptor<SignUpUserUseCaseInput> inputCaptor =
                ArgumentCaptor.forClass(SignUpUserUseCaseInput.class);

        verify(signUpUserUseCase).execute(inputCaptor.capture());

        SignUpUserUseCaseInput input = inputCaptor.getValue();

        assertThat(input.getName()).isEqualTo(NAME);
        assertThat(input.getEmail()).isEqualTo(EMAIL);
        assertThat(input.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("Deve autenticar usuário e retornar status 200")
    void shouldSignInUserAndReturnOk() throws Exception {
        when(signInUserUseCase.execute(any(SignInUserUseCaseInput.class)))
                .thenReturn(JWT_TOKEN);

        String requestBody = signInRequest(EMAIL, PASSWORD);

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(JWT_TOKEN))
                .andExpect(jsonPath("$.message").value("Ok"));

        ArgumentCaptor<SignInUserUseCaseInput> inputCaptor =
                ArgumentCaptor.forClass(SignInUserUseCaseInput.class);

        verify(signInUserUseCase).execute(inputCaptor.capture());

        SignInUserUseCaseInput input = inputCaptor.getValue();

        assertThat(input.getEmail()).isEqualTo(EMAIL);
        assertThat(input.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("Não deve cadastrar usuário quando nome for inválido")
    void shouldNotSignUpUserWhenNameIsInvalid() throws Exception {
        String requestBody = signUpRequest(INVALID_NAME, EMAIL, PASSWORD);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(signUpUserUseCase);
    }

    @Test
    @DisplayName("Não deve cadastrar usuário quando e-mail for inválido")
    void shouldNotSignUpUserWhenEmailIsInvalid() throws Exception {
        String requestBody = signUpRequest(NAME, INVALID_EMAIL, PASSWORD);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(signUpUserUseCase);
    }

    @Test
    @DisplayName("Não deve cadastrar usuário quando senha for inválida")
    void shouldNotSignUpUserWhenPasswordIsInvalid() throws Exception {
        String requestBody = signUpRequest(NAME, EMAIL, INVALID_PASSWORD);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(signUpUserUseCase);
    }

    private String signUpRequest(
            String name,
            String email,
            String password
    ) {
        return """
        {
            "name": "%s",
            "email": "%s",
            "password": "%s"
        }
        """.formatted(name, email, password);
    }

    private String signInRequest(
            String email,
            String password
    ) {
        return """
        {
            "email": "%s",
            "password": "%s"
        }
        """.formatted(email, password);
    }
}