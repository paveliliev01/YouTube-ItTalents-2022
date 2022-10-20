package com.youtube_project.model.relationships.commentsreactions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class CommentReactionKey implements Serializable {

        @Column(name = "comment_id")
        private Long commentId;

        @Column(name = "user_id")
        private Long userId;

}
