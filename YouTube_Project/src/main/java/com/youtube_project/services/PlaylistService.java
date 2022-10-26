package com.youtube_project.services;

import com.youtube_project.model.dtos.playlist.PlaylistAddDTO;
import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.entities.Playlist;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import com.youtube_project.model.relationships.playlistshasvideos.VideosInPlaylist;
import com.youtube_project.model.relationships.playlistshasvideos.VideosInPlaylistKey;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlaylistService extends AbstractService {
    public PlaylistDTO addPlaylist(PlaylistAddDTO playlistAddDTO, long userId) {
        Playlist playlist = new Playlist();
        playlist.setDateOfCreation(LocalDateTime.now());
        playlist.setOwner(getUserById(userId));
        playlist.setPrivate(playlistAddDTO.isPrivate());
        playlist.setName(playlistAddDTO.getName());
        playlistRepository.save(playlist);
        return mapToPlayListDTO(playlist);
    }


    private PlaylistDTO mapToPlayListDTO(Playlist playlist) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setUserResponseDTO(modelMapper.map(playlist.getOwner(), UserResponseDTO.class));
        playlistDTO.setName(playlist.getName());
        playlistDTO.setId(playlist.getId());


        return playlistDTO;
    }

    public boolean addVideoToPlaylist(long playlistId, long videoId, long uid) {
        Playlist playlist = getPlaylistById(playlistId);
        Video video = getVideoById(videoId);
        User user = getUserById(uid);
        if (!user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("This is not your playlist!");
        }
        if (playlist.getVideos().contains(video)) {
            throw new BadRequestException("Video already exists in playlist");
        }
        if (video.isPrivate()){
            throw new UnauthorizedException("The video you want to add to this playlist is private!");
        }
        videosInPlaylistRepository.save(getVideoInPlaylist(playlistId, videoId));
        return true;
    }


    public boolean deleteVideoFromPlaylist(long playlistId, long videoId, long uid) {
        Playlist playlist = getPlaylistById(playlistId);
        Video video = getVideoById(videoId);
        User user = getUserById(uid);
        if (!user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("This is not your playlist!");
        }
        if (!playlist.getVideos().contains(video)) {
            throw new BadRequestException("Video already doesn't exist exists in playlist");
        }

        videosInPlaylistRepository.delete(getVideoInPlaylist(playlistId, videoId));
        return true;
    }

    public List<PlaylistDTO> getPlaylistByName(String name) {

        List<Playlist> playlist = playlistRepository.findAllByName(name).orElseThrow(() -> new NotFoundException("Playlist not found"));
        List<PlaylistDTO> playlistDTOS = new ArrayList<>();
        Set<VideoResponseDTO> videoDTOS = new HashSet<>();
        for (Playlist playlist1 : playlist) {
            if(playlist1.isPrivate()){
                continue;
            }
            getPlaylist(playlistDTOS, videoDTOS, playlist1);
        }
        return playlistDTOS;
    }

    private void getPlaylist(List<PlaylistDTO> playlistDTOS, Set<VideoResponseDTO> videoDTOS, Playlist playlist1) {
        PlaylistDTO playlistDTO = mapToPlayListDTO(playlist1);
        Set<Video> videos = playlist1.getVideos();
        for (Video video : videos) {
            if (video.isPrivate()){
                videos.remove(video);
                playlistRepository.save(playlist1);
                continue;
            }
            videoDTOS.add(videoToResponseVideoDTO(video));
        }
        playlistDTO.setVideos(videoDTOS);
        playlistDTOS.add(playlistDTO);
    }

    public String deletePlaylist(long playlistId, long uid) {

        Playlist playlist = getPlaylistById(playlistId);
        User user = getUserById(uid);

        if (!user.getPlaylists().contains(playlist)) {
            throw new BadRequestException("This is not your playlist!");
        }

        playlistRepository.delete(playlist);

        return "Playlist " + playlist.getName() + " was deleted";
    }

    private VideosInPlaylist getVideoInPlaylist(long playlistId, long videoId) {

        Playlist playlist = getPlaylistById(playlistId);
        Video video = getVideoById(videoId);
        VideosInPlaylistKey videosInPlaylistKey = new VideosInPlaylistKey();
        videosInPlaylistKey.setPlaylistId(playlistId);
        videosInPlaylistKey.setVideoId(videoId);

        VideosInPlaylist videosInPlaylist = new VideosInPlaylist();
        videosInPlaylist.setVideosInPlaylistKey(videosInPlaylistKey);
        videosInPlaylist.setPlaylist(playlist);
        videosInPlaylist.setVideo(video);
        videosInPlaylist.setDateOfAdding(LocalDateTime.now());

        return videosInPlaylist;
    }

    public List<PlaylistDTO> getAllMyPlaylists(long uid){
        List<Playlist> playlist = playlistRepository.findAllByOwner(getUserById(uid));
        List<PlaylistDTO> playlistDTOS = new ArrayList<>();
        Set<VideoResponseDTO> videoDTOS = new HashSet<>();
        for (Playlist playlist1 : playlist) {
            getPlaylist(playlistDTOS, videoDTOS, playlist1);
        }
        return playlistDTOS;
    }

}
