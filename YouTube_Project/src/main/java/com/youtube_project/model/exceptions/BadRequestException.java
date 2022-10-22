package com.youtube_project.model.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg){
        super(msg);
    }

    public BadRequestException(String msg,Throwable cause){
        super(msg,cause);
    }

    /**
     * Gets a result.
     *
     * @return a result
     */

}
