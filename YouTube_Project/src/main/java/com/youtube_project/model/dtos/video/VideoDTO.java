package com.youtube_project.model.dtos.video;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDate;
@Data
public class VideoDTO {
    private long id;
    private String title;
    private String description;
    private UserResponseDTO owner;
    private LocalDate dateOfUpload;

}
