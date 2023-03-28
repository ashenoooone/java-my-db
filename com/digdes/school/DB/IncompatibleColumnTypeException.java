package com.digdes.school.DB;

public class IncompatibleColumnTypeException extends RuntimeException {
    public IncompatibleColumnTypeException(String message) {
        super(message);
    }
}
