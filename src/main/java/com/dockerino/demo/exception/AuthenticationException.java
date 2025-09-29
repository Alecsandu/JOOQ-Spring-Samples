package com.dockerino.demo.exception;

/**
 * Thrown by classes which handle authentication of the user.
 */
public class AuthenticationException extends RuntimeException{

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
