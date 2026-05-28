package com.api.presentation.user.controller;

import com.api.application.user.dto.SignInUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseOutput;
import com.api.application.user.useCase.SignInUserUseCase;
import com.api.application.user.useCase.SignUpUserUseCase;
import com.api.presentation.shared.dto.DefaultResponseDto;
import com.api.presentation.user.dto.SignInResponseDto;
import com.api.presentation.user.dto.UserResponseDto;
import com.api.presentation.user.dto.UserSignInRequestDto;
import com.api.presentation.user.dto.UserSignUpRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignUpUserUseCase signUpUserUseCase;
    private final SignInUserUseCase signInUserUseCase;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponseDto signUp(@Valid @RequestBody UserSignUpRequestDto request) {
        SignUpUserUseCaseInput input = SignUpUserUseCaseInput.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        SignUpUserUseCaseOutput output = signUpUserUseCase.execute(input);

        return DefaultResponseDto.builder()
                .id(output.getId())
                .message("Created")
                .build();
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public SignInResponseDto signIn(@RequestBody UserSignInRequestDto request) {
        SignInUserUseCaseInput input = SignInUserUseCaseInput.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        String token = signInUserUseCase.execute(input);

        return SignInResponseDto.builder()
                .accessToken(token)
                .message("Ok")
                .build();
    }
}
