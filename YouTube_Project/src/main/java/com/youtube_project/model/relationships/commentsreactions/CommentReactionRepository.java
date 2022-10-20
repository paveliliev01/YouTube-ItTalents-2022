package com.youtube_project.model.relationships.commentsreactions;

import com.youtube_project.model.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReactionKey> {
    List<CommentReaction> findAllByCommentAndReaction(Comment comment, char reaction);
}
