package com.youtube_project.model.repositories;

import com.youtube_project.model.entities.Playlist;
import com.youtube_project.model.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
   @Query("select p from Playlist p where p.name LIKE %:name%")
   List<Playlist> findAllByName(String name, Pageable page);
   List<Playlist> findAllByOwner(User user);
}
