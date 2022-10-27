package com.youtube_project.model.dtos;

import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SearchDTO {
    List<UserResponseDTO> users;
    List<VideoResponseDTO> videos;
    List<PlaylistDTO> playlists;
}
