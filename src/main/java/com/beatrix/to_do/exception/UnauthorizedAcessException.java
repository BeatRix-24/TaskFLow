package com.beatrix.to_do.exception;

public class UnauthorizedAcessException extends RuntimeException{
    public UnauthorizedAcessException(String message){
        super(message);
    }
}
