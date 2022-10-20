package com.youtube_project.model.dtos.video;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VideoWithNoOwnerDTO {
    private int id;
    private String title;
    private String description;
    private LocalDate dateOfUpload;
}
