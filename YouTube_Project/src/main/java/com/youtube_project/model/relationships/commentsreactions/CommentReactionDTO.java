package com.youtube_project.model.relationships.commentsreactions;

import com.youtube_project.model.dtos.user.UserResponseDTO;
import lombok.Data;

@Data
public class CommentReactionDTO {

    private UserResponseDTO user;
    private CommentReactionDTO commentReactionDTO;

}
