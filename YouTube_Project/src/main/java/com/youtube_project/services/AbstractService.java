package com.youtube_project.services;

import com.youtube_project.model.dtos.SearchDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.dtos.video.VideoResponseDTO;
import com.youtube_project.model.dtos.video.VideoWithNoOwnerDTO;
import com.youtube_project.model.entities.*;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.relationships.commentsreactions.CommentReactionRepository;
import com.youtube_project.model.relationships.playlistshasvideos.VideosInPlaylistRepository;
import com.youtube_project.model.relationships.videoreactions.VideoReactionRepository;
import com.youtube_project.model.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    protected CategoryRepository categoryRepository;
    @Autowired
    protected CommentReactionRepository commentReactionRepository;
    @Autowired
    protected VideosInPlaylistRepository videosInPlaylistRepository;
    @Autowired
    protected JavaMailSender javaMailSender;

    public static final char LIKE = 'l';
    public static final char DISLIKE = 'd';

    protected User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    protected Video getVideoById(long id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

    protected Comment getCommentById(long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    protected Playlist getPlaylistById(long id){
        return playlistRepository.findById(id).orElseThrow(()-> new NotFoundException("Playlist not found"));
    }

    protected UserResponseDTO userToUserResponseDTO(User u){
        return modelMapper.map(u,UserResponseDTO.class);
    }

    protected Optional<Category> getCategoryByNameOptional(String name){
        return categoryRepository.findByName(name);
    }
    protected Category getCategoryByName(String name){
        return categoryRepository.findByName(name).orElseThrow(() -> new NotFoundException("Category not found!"));
    }

    protected User getUserByEmail(String email){
        return userRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException("Incorrect e-mail!"));
    }

    protected VideoResponseDTO videoToResponseVideoDTO(Video v) {
        VideoResponseDTO vDTO = modelMapper.map(v,VideoResponseDTO.class);
        vDTO.setOwner(modelMapper.map(v.getOwner(), UserResponseDTO.class));
        vDTO.setLikes(videoReactionRepository.findAllByVideoAndReaction(v,LIKE).size());
        vDTO.setDislikes(videoReactionRepository.findAllByVideoAndReaction(v,DISLIKE).size());
        vDTO.setViews(v.getViewers().size());
        return vDTO;
    }

}
