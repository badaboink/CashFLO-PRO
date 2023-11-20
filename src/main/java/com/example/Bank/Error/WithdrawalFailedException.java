package com.example.Bank.Error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR )
public class WithdrawalFailedException extends RuntimeException {
    public WithdrawalFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}