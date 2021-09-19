package com.honesty.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RegistrationException {
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateUserException extends RuntimeException{
        public DuplicateUserException(String msg){super(msg);}
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EmailNotFoundException extends RuntimeException{
        public EmailNotFoundException(String message) {
            super(message);
        }
    }
}
