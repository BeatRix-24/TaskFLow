package com.beatrix.taskflow.exception;

public class UnauthorizedAcessException extends RuntimeException{
    public UnauthorizedAcessException(String message){
        super(message);
    }
}
