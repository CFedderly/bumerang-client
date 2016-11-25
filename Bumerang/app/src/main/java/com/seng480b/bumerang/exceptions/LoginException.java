package com.seng480b.bumerang.exceptions;

public class LoginException extends Exception {
    // Empty constructor
    public LoginException()  {}

    public LoginException(String message) {
        super(message);
    }
}
