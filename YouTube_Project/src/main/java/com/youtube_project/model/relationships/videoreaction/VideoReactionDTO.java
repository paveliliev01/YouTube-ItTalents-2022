package com.youtube_project.model.relationships.videoreaction;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoWithNoOwnerDTO;
import lombok.Data;

@Data
public class VideoReactionDTO {

    private UserResponseDTO user;
    private VideoWithNoOwnerDTO video;

}
