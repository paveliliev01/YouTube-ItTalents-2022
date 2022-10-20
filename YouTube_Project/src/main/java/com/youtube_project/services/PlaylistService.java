package com.youtube_project.services;

import com.youtube_project.model.dtos.playlist.PlaylistAddDTO;
import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.entities.Playlist;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlaylistService extends AbstractService {
    public PlaylistDTO addPlaylist(PlaylistAddDTO playlistAddDTO, long userId) {
        Playlist playlist = new Playlist();
        playlist.setDateOfCreation(LocalDateTime.now());
        playlist.setUser(getUserById(userId));
        playlist.setPrivate(playlistAddDTO.isPrivate());
        playlist.setName(playlistAddDTO.getName());
        playlistRepository.save(playlist);
        return mapToPlayListDTO(playlist);
    }

    private PlaylistDTO mapToPlayListDTO(Playlist playlist){
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setUserResponseDTO(modelMapper.map(playlist.getUser(), UserResponseDTO.class));
        playlistDTO.setName(playlistDTO.getName());
        playlistDTO.setId(playlist.getId());
        return playlistDTO;
    }
}
