package com.youtube_project.models.exceptions;

import java.util.function.Supplier;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){
        super(msg);
    }

}
