package com.youtube_project.models.util;

import com.youtube_project.models.comment.CommentRepository;
import com.youtube_project.models.exceptions.NotFoundException;
import com.youtube_project.models.playlist.PlaylistRepository;
import com.youtube_project.models.relationships.videoreaction.VideoReactionRepository;
import com.youtube_project.models.user.User;
import com.youtube_project.models.user.UserRepository;
import com.youtube_project.models.video.Video;
import com.youtube_project.models.video.VideoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbstractService {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected VideoReactionRepository videoReactionRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected VideoRepository videoRepository;
    @Autowired
    protected PlaylistRepository playlistRepository;

    protected User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
    protected Video getVideoById(long id){
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

}
