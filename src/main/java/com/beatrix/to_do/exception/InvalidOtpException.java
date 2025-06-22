package com.beatrix.to_do.exception;

public class InvalidOtpException extends RuntimeException{
    public InvalidOtpException (String message) {
        super(message);
    }
}
