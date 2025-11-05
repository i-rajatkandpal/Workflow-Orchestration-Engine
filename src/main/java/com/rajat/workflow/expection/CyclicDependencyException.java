package com.rajat.workflow.expection;

public class CyclicDependencyException extends RuntimeException{
    public CyclicDependencyException(String message){
        super(message);
    }
}
