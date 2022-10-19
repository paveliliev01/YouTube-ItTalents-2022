package com.youtube_project.models.exceptions;

public class UnauthorizedException extends RuntimeException{


    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
