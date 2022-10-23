package com.youtube_project.model.dtos.category;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.Set;
@Data
public class CategoryDTO {
    private long id;
    private String name;
    private LocalDate creationDate;
    private Set<UserResponseDTO> followers;
    private Set<VideoResponseDTO> videos;
}
