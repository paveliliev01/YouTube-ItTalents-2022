package com.youtube_project.services;

import com.youtube_project.model.dtos.comment.CommentAddDTO;
import com.youtube_project.model.dtos.comment.CommentDTO;
import com.youtube_project.model.entities.Comment;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.entities.Video;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService extends AbstractService {
    public CommentDTO addComment(CommentAddDTO commentAddDTO, long videoId, long userId) {
        Comment comment = new Comment();
        Video video = getVideoById(videoId);
        User u = getUserById(userId);
        comment.setDateOfCreation(LocalDateTime.now());
        comment.setOwner(u);
        comment.setVideo(video);
        comment.setText(commentAddDTO.getText());
        commentRepository.save(comment);
        return modelMapper.map(comment, CommentDTO.class);
    }

    public CommentDTO getCommentById(long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new NotFoundException("Comment not found"));
        return modelMapper.map(comment,CommentDTO.class);
    }
}
