package com.dockerino.demo.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
