package com.api.application.user.useCase;

import com.api.application.user.dto.SignInUserUseCaseInput;
import com.api.domain.user.entity.User;
import com.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class SignInUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public String execute(SignInUserUseCaseInput input) {

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return tokenService.generate(user);
    }
}