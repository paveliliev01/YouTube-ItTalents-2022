package com.youtube_project.services;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.entities.Comment;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.relationships.commentsreactions.CommentReactionRepository;
import com.youtube_project.model.relationships.videoreactions.VideoReactionRepository;
import com.youtube_project.model.repositories.CommentRepository;
import com.youtube_project.model.repositories.PlaylistRepository;
import com.youtube_project.model.repositories.UserRepository;
import com.youtube_project.model.repositories.VideoRepository;
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
    @Autowired
    protected CommentReactionRepository commentReactionRepository;

    protected User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected Video getVideoById(long id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

    protected Comment getCommentById(long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    protected UserResponseDTO userToUserResponseDTO(User u){
        return modelMapper.map(u,UserResponseDTO.class);
    }

}
