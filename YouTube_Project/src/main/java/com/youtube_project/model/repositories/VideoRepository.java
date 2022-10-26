package com.youtube_project.model.repositories;

import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {

    @Query("select v from Video v where v.title LIKE %:title%")
    List<Video> findAllByTitle(String title, Pageable page);
    List<Video> findAllByOwner(User user);
}
