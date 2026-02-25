package com.dockerino.demo.exception.authentication;

public class UserExistsException extends AuthenticationException {
    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
