package com.youtube_project.contollers;

import com.youtube_project.model.dtos.playlist.PlaylistAddDTO;
import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.repositories.PlaylistRepository;
import com.youtube_project.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController extends MasterController{

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistDTO createPlaylist(@RequestBody PlaylistAddDTO playlist, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return playlistService.addPlaylist(playlist,sessionManager.getSessionUserId(request));
    }

    @PostMapping("/add/video")
    @ResponseStatus(HttpStatus.OK)
    public boolean addVideoToPlaylist(@RequestParam long playlistId,@RequestParam long videoId,HttpServletRequest request){
        sessionManager.validateLogin(request);
        return playlistService.addVideoToPlaylist(playlistId,videoId, sessionManager.getSessionUserId(request));
    }

    @DeleteMapping("/delete/video")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteVideoFromPlaylist(@RequestParam long playlistId, @RequestParam long videoId, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return playlistService.deleteVideoFromPlaylist(playlistId, videoId, sessionManager.getSessionUserId(request));
    }

    @GetMapping("/getByName")
    @ResponseStatus(HttpStatus.OK)
    public List<PlaylistDTO> getAllByName(@RequestParam("name") String name) {
        return playlistService.getPlaylistByName(name);
    }

    @DeleteMapping("/delete/playlist")
    @ResponseStatus(HttpStatus.OK)
    public String deletePlaylist(@RequestParam long playlistId, HttpServletRequest request) {
        sessionManager.validateLogin(request);
        return playlistService.deletePlaylist(playlistId, sessionManager.getSessionUserId(request));
    }


}
