package com.youtube_project.models.exceptions;

import java.util.function.Supplier;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg){
        super(msg);
    }

    /**
     * Gets a result.
     *
     * @return a result
     */

}
