package com.api.application.user.port;

public interface CryptoService {
    String encode(String password);
    boolean matches(String match1, String match2);
}
