package com.rajat.workflow.exception;

public class CyclicDependencyException extends RuntimeException{
    public CyclicDependencyException(String message){
        super(message);
    }
}
