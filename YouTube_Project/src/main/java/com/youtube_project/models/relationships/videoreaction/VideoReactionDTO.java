package com.youtube_project.models.relationships.videoreaction;

import com.youtube_project.models.user.UserResponseDTO;
import com.youtube_project.models.video.VideoWithNoOwnerDTO;
import lombok.Data;

@Data
public class VideoReactionDTO {

    private UserResponseDTO user;
    private VideoWithNoOwnerDTO video;

}
