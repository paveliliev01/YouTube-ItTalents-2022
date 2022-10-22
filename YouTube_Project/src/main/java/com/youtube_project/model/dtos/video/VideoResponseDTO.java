package com.youtube_project.model.dtos.video;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoResponseDTO {
    private int id;
    private String title;
    private UserResponseDTO user;
    private LocalDateTime uploadDate;
    private String videoUrl;
    private boolean isPrivate;
    private int views;
    private int likes;
    private int dislikes;
}
