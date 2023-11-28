package com.example.inginerie_software.exceptions;

public class bikes_typeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public bikes_typeException(String s) {
        super(s);


    }


    public Long getCode() {
        return serialVersionUID;
    }
}
