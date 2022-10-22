package com.youtube_project.model.repositories;

import com.youtube_project.model.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
   Optional<List<Playlist>> findAllByName(String name);
}
