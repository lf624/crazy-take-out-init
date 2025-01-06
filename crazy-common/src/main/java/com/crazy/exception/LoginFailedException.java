package com.crazy.exception;

public class LoginFailedException extends BaseException {
    public LoginFailedException() {
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
