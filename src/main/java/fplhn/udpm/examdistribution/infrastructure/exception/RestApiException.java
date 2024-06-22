package fplhn.udpm.examdistribution.infrastructure.exception;

import lombok.Setter;

import java.io.Serial;

@Setter
public class RestApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private String message;

    public RestApiException() {
    }

    public RestApiException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
