package com.youtube_project.services;

import com.youtube_project.model.dtos.playlist.PlaylistAddDTO;
import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoDTO;
import com.youtube_project.model.entities.Playlist;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
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

        videosInPlaylistRepository.save(getVideoInPlaylist(playlistId, videoId));
//        Set<Playlist> playlists = user.getPlaylists();
//        for (Playlist playlist1 : playlists) {
//            System.out.println(playlist1.getName());
//            for (Video video1 : playlist1.getVideos()) {
//                System.out.println(video1.getTitle());
//            }
//        }
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
//        Set<Playlist> playlists = user.getPlaylists();
//        for (Playlist playlist1 : playlists) {
//            System.out.println(playlist1.getName());
//            for (Video video1 : playlist1.getVideos()) {
//                System.out.println(video1.getTitle());
//            }
//        }
        return true;
    }

    public List<PlaylistDTO> getPlaylistByName(String name) {

        List<Playlist> playlist = playlistRepository.findAllByName(name).orElseThrow(() -> new NotFoundException("Playlist not found"));
        List<PlaylistDTO> playlistDTOS = new ArrayList<>();
        Set<VideoDTO> videoDTOS = new HashSet<>();
        VideoDTO videoDTO;



        for (Playlist playlist1 : playlist) {
            if(playlist1.isPrivate()){
                continue;
            }
            PlaylistDTO playlistDTO = mapToPlayListDTO(playlist1);
            Set<Video> videos = playlist1.getVideos();
            for (Video video : videos) {
                videoDTO = modelMapper.map(video, VideoDTO.class);
                UserResponseDTO u = userToUserResponseDTO(video.getOwner());
                videoDTO.setOwner(u);
                videoDTOS.add(videoDTO);
            }
            playlistDTO.setVideos(videoDTOS);
            playlistDTOS.add(playlistDTO);
        }
        return playlistDTOS;
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

}
