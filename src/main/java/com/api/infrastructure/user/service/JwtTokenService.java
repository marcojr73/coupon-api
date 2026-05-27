package com.api.infrastructure.user.service;

import com.api.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private static final String SECRET = "coupom-api-secret";
    private static final long EXP = 86400;

    public String generate(User user) {
    }
}
