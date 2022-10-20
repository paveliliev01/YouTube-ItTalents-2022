package com.youtube_project.model.relationships.commentsreactions;

import com.youtube_project.model.entities.Comment;
import com.youtube_project.model.entities.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name = "comments_have_reactions")
@Data
@EqualsAndHashCode
public class CommentReaction {

    @EmbeddedId
    private CommentReactionKey id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name ="reaction")
    private char reaction;


}
