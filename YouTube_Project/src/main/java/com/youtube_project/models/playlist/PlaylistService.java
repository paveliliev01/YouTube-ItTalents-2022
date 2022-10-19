package com.youtube_project.models.playlist;

import com.youtube_project.models.user.UserResponseDTO;
import com.youtube_project.models.util.AbstractService;
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
