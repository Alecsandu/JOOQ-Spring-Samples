package com.dockerino.demo.exception.authentication;

/**
 * Thrown by classes which handle authentication of the user.
 */
public class AuthenticationException extends RuntimeException{

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
