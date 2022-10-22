package com.youtube_project.services;

import com.youtube_project.model.dtos.comment.CommentAddDTO;
import com.youtube_project.model.dtos.comment.CommentDTO;
import com.youtube_project.model.dtos.comment.CommentResponseDTO;
import com.youtube_project.model.dtos.user.UserResponseDTO;
import com.youtube_project.model.entities.Comment;
import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import com.youtube_project.model.exceptions.BadRequestException;
import com.youtube_project.model.exceptions.NotFoundException;
import com.youtube_project.model.exceptions.UnauthorizedException;
import com.youtube_project.model.relationships.commentsreactions.CommentReaction;
import com.youtube_project.model.relationships.commentsreactions.CommentReactionKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setOwner(modelMapper.map(comment.getOwner(), UserResponseDTO.class));

        return commentDTO;
    }

    public CommentDTO getById(long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
        return modelMapper.map(comment, CommentDTO.class);
    }

    private CommentResponseDTO getCommentAndSubComments(long cid) {
        Comment comment = getCommentById(cid);
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setText(comment.getText());
        commentResponseDTO.setId(comment.getId());
        commentResponseDTO.setOwner(userToUserResponseDTO(comment.getOwner()));

        List<CommentDTO> subComments = comment.getSubComments().stream().map(this::commentToCommentDTO).collect(Collectors.toList());
        commentResponseDTO.setSubComments(subComments);

        return commentResponseDTO;
    }

    public List<CommentResponseDTO> getAllCommentsOfVideo(long vid){
        List<Comment> commentsOfVideo = getVideoById(vid).getComments();
        List<CommentResponseDTO> commentDTOS = new ArrayList<>();
        for (Comment comment : commentsOfVideo) {
            commentDTOS.add(getCommentAndSubComments(comment.getId()));
        }

        return commentDTOS;
    }

    @Transactional(rollbackForClassName = "SQLException.class")
    public void deleteById(long vid,long cid, long uid) {
        Video video = getVideoById(vid);
        Comment comment = getCommentById(cid);
        if (comment.getOwner().getId() != uid){
            throw new UnauthorizedException("This is not your comment to delete!");
        }
        if (!video.getComments().contains(comment)){
            throw new BadRequestException("The comment you are trying to delete doesn't exist");
        }
        commentRepository.deleteById(cid);
    }

    public boolean reactToComment(long cid, long uid,char reaction){
        Comment comment = getCommentById(cid);
        User user = getUserById(uid);
        CommentReactionKey commentReactionKey = new CommentReactionKey();
        commentReactionKey.setCommentId(cid);
        commentReactionKey.setUserId(uid);

        CommentReaction commentReaction = new CommentReaction();
        commentReaction.setId(commentReactionKey);
        commentReaction.setComment(comment);
        commentReaction.setUser(user);
        commentReaction.setReaction(reaction);

        if (commentReactionRepository.findById(commentReactionKey).isPresent()
                && commentReactionRepository.findById(commentReactionKey).get().getReaction() == reaction){
            commentReactionRepository.delete(commentReaction);
            return false;
        }

        commentReactionRepository.save(commentReaction);
        return true;
    }

    public CommentDTO replyToAComment(CommentAddDTO commentAddDTO, long cid, long uid) {
        Comment comment = getCommentById(cid);
        Comment commentReply = modelMapper.map(commentAddDTO,Comment.class);

        commentReply.setOwner(getUserById(uid));
        commentReply.setText(commentReply.getText());
        commentReply.setVideo(comment.getVideo());
        commentReply.setParent(comment);
        commentReply.setDateOfCreation(LocalDateTime.now());

        commentRepository.save(commentReply);

        return commentToCommentDTO(commentReply);

    }

    private CommentDTO commentToCommentDTO(Comment comment){
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setOwner(userToUserResponseDTO(comment.getOwner()));
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setDateOfCreation(comment.getDateOfCreation());

        int likes = commentReactionRepository.findAllByCommentAndReaction(comment,'l').size();
        int dislikes = commentReactionRepository.findAllByCommentAndReaction(comment,'d').size();

        commentDTO.setLikes(likes);
        commentDTO.setDislikes(dislikes);

        return commentDTO;
    }
}
