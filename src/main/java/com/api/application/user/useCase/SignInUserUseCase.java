package com.api.application.user.useCase;

import com.api.core.exception.RestException;
import com.api.application.user.port.TokenService;
import com.api.domain.user.repository.UserRepository;
import com.api.application.user.dto.SignInUserUseCaseInput;
import com.api.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignInUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public SignInUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String execute(SignInUserUseCaseInput input) {

        User user = userRepository.findByEmail(input.getEmail()).orElseThrow(()
                -> RestException.unprocessableEntity("Email or password is incorrect"));

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw RestException.unprocessableEntity("Email or password is incorrect");
        }

        return tokenService.generate(user);
    }
}