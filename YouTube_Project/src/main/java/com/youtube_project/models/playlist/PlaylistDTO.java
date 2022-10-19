package com.youtube_project.models.playlist;

import com.youtube_project.models.user.UserResponseDTO;
import lombok.Data;

@Data
public class PlaylistDTO {
    private long id;
    private String name;
    private boolean isPrivate;
    private UserResponseDTO userResponseDTO;
}
