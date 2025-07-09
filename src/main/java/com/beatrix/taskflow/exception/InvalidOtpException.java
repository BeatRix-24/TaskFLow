package com.beatrix.taskflow.exception;

public class InvalidOtpException extends RuntimeException{
    public InvalidOtpException (String message) {
        super(message);
    }
}
