package com.youtube_project.services;

import com.youtube_project.model.dtos.SearchDTO;
import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService extends AbstractService{
    @Autowired
    private PlaylistService playlistService;

    public SearchDTO searchByName(String name, Pageable page) {
        SearchDTO searchDTO = new SearchDTO();
        List<VideoResponseDTO> videoDto = videoRepository.findAllByTitle(name,page).stream().map(this::videoToResponseVideoDTO).collect(Collectors.toList());
        List<UserResponseDTO> usersDto = userRepository.findAllByFullNameContaining(name,page).stream().map(u -> modelMapper.map(u,UserResponseDTO.class)).collect(Collectors.toList());
        List<PlaylistDTO> playlistDTOS = playlistService.getPlaylistByName(name,page);
        searchDTO.setVideos(videoDto);
        searchDTO.setUsers(usersDto);
        searchDTO.setPlaylists(playlistDTOS);
        return searchDTO;
    }
}
