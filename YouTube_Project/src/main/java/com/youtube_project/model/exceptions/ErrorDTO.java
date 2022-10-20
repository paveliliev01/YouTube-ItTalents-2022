package com.youtube_project.model.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {

    private int status;
    private LocalDateTime time;
    private String msg;
}
