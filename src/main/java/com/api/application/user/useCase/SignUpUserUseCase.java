package com.api.application.user.useCase;

import com.api.core.exception.RestException;
import com.api.application.user.port.CryptoService;
import com.api.domain.user.repository.UserRepository;
import com.api.application.user.dto.SignUpUserUseCaseInput;
import com.api.application.user.dto.SignUpUserUseCaseOutput;
import com.api.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class SignUpUserUseCase {

    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    public SignUpUserUseCase(
            UserRepository userRepository,
            CryptoService cryptoService
    ) {
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }

    public SignUpUserUseCaseOutput execute(SignUpUserUseCaseInput input) {

        userRepository.findByEmail(input.getEmail()).ifPresent(existing -> {
            throw RestException.conflict("Email already in use");
        });

        String encryptedPassword = cryptoService.encode(input.getPassword());

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