package com.example.bookInventory.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(){
        super("Application Resource not found");
    }

    public ResourceNotFoundException(String message){
        super(message);
    }

}
