package com.dockerino.demo.exception.authentication;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException() {
        super("Invalid token");
    }

}
