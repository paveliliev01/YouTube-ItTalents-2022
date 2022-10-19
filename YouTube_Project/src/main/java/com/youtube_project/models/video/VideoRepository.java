package com.youtube_project.models.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {

    Set<Video> findAllByTitle(String title);

}
