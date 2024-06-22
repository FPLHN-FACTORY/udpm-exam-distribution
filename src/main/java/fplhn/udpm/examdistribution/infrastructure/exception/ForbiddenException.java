package fplhn.udpm.examdistribution.infrastructure.exception;

import lombok.Setter;

import java.io.Serial;

@Setter
public class ForbiddenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private String message;

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
