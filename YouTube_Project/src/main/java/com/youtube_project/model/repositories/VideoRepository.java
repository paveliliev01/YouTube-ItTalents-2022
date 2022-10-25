package com.youtube_project.model.repositories;

import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {

    List<Video> findAllByTitle(String title);
    List<Video> findAllByOwner(User user);
}
