package com.youtube_project.contollers;

import com.youtube_project.model.dtos.playlist.PlaylistDTO;
import com.youtube_project.model.repositories.PlaylistRepository;
import com.youtube_project.model.dtos.playlist.PlaylistAddDTO;
import com.youtube_project.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PlaylistController extends MasterController{
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/playlist/add")
    public PlaylistDTO createPlaylist(@RequestBody PlaylistAddDTO playlist, HttpServletRequest request){
        sessionManager.validateLogin(request);
        return playlistService.addPlaylist(playlist,sessionManager.getSessionUserId(request));
    }

}
