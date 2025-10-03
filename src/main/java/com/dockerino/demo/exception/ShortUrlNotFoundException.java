package com.dockerino.demo.exception;

public class ShortUrlNotFoundException extends ResourceNotFoundException {

    public ShortUrlNotFoundException() {
        super("ShortUrl not found");
    }

}
