package com.youtube_project.models.relationships.videoreaction;

import com.youtube_project.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoReactionRepository extends JpaRepository<VideoReaction, VideoReactionKey> {
    List<VideoReaction> findAllByUserAndReaction(User videoId,char reaction);
}
