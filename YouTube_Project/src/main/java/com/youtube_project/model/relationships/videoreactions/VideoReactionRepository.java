package com.youtube_project.model.relationships.videoreactions;

import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoReactionRepository extends JpaRepository<VideoReaction, VideoReactionKey> {
    List<VideoReaction> findAllByUserAndReaction(User videoId, char reaction);

    List<VideoReaction> findAllByVideoAndReaction(Video v,char reaction);

}
