package com.api.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends RuntimeException {

    private final HttpStatus status;

    public RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static RestException badRequest(String message) {
        return new RestException(HttpStatus.BAD_REQUEST, message);
    }

    public static RestException unauthorized(String message) {
        return new RestException(HttpStatus.UNAUTHORIZED, message);
    }

    public static RestException forbidden(String message) {
        return new RestException(HttpStatus.FORBIDDEN, message);
    }

    public static RestException notFound(String message) {
        return new RestException(HttpStatus.NOT_FOUND, message);
    }

    public static RestException conflict(String message) {
        return new RestException(HttpStatus.CONFLICT, message);
    }

    public static RestException unprocessableEntity(String message) {
        return new RestException(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }

    public static RestException internalServerError(String message) {
        return new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}