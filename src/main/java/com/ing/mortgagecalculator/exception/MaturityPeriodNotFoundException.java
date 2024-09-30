package com.ing.mortgagecalculator.exception;

public class MaturityPeriodNotFoundException extends RuntimeException {
    public MaturityPeriodNotFoundException(String message) {
        super(message);
    }
}
