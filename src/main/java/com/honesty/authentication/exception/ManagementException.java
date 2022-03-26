package com.honesty.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ManagementException {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class TokenNotFound extends RuntimeException{
        public TokenNotFound(String message) {
            super(message);
        }
    }


    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public static class TokenExpired extends RuntimeException{
        public TokenExpired(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class NotSameUser extends RuntimeException{
        public NotSameUser(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public static class PasswordDoesNotMatch extends RuntimeException{
        public PasswordDoesNotMatch(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class UserNotFound extends RuntimeException{
        public UserNotFound(String message) {
            super(message);
        }
    }
}
