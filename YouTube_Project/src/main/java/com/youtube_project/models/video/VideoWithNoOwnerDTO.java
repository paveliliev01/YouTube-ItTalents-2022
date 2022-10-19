package com.youtube_project.models.video;

import com.youtube_project.models.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VideoWithNoOwnerDTO {
    private int id;
    private String title;
    private String description;
    private LocalDate dateOfUpload;
}
