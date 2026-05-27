package com.api.application.user.useCase;

import com.api.application.user.dto.SignUpUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseOutput;
import com.api.core.exception.ConflictException;
import com.api.domain.user.entity.User;
import com.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpUserUseCaseOutput execute(SignUpUserUseCaseInput input) {

        userRepository.findByEmail(input.getEmail()).ifPresent(existing -> {
            throw new ConflictException("Email already in use");
        });

        String encryptedPassword =
                passwordEncoder.encode(input.getPassword());

        User user = User.builder()
                .name(input.getName())
                .email(input.getEmail())
                .password(encryptedPassword)
                .build();

        User created = userRepository.create(user);

        return SignUpUserUseCaseOutput.builder()
                .id(created.getId())
                .name(created.getName())
                .email(created.getEmail())
                .build();
    }
}