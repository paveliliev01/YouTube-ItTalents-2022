package com.youtube_project.model.dtos.playlist;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import lombok.Data;

import java.util.Set;

@Data
public class PlaylistDTO {
    private long id;
    private String name;
    private boolean isPrivate;
    private UserResponseDTO owner;
    private Set<VideoResponseDTO> videos;
}
