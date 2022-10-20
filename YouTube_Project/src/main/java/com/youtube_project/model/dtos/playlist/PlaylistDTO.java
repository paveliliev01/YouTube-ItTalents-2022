package com.youtube_project.model.dtos.playlist;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import lombok.Data;

@Data
public class PlaylistDTO {
    private long id;
    private String name;
    private boolean isPrivate;
    private UserResponseDTO userResponseDTO;
}
