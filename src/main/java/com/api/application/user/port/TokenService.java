package com.api.application.user.port;

import com.api.domain.user.entity.User;

public interface TokenService {
    String generate(User password);
}
