package com.youtube_project.services;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.repositories.CommentRepository;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.repositories.PlaylistRepository;
import com.youtube_project.model.relationships.videoreaction.VideoReactionRepository;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.repositories.UserRepository;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.repositories.VideoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    protected BCryptPasswordEncoder passwordEncoder;

    protected User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
    public List<UserResponseDTO> getAllUsersByName(String firstName, String lastName) {
        return userRepository.findAllByFirstNameAndLastName(firstName, lastName).stream().map(u -> modelMapper.map(u, UserResponseDTO.class)).collect(Collectors.toList());
    }
    protected Video getVideoById(long id){
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

}
