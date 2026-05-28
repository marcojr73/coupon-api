package com.api.infrastructure.user.service;

import com.api.application.user.port.CryptoService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptService implements CryptoService {

    private final PasswordEncoder passwordEncoder;

    public BCryptService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(String text) {
        return passwordEncoder.encode(text);
    }
}
