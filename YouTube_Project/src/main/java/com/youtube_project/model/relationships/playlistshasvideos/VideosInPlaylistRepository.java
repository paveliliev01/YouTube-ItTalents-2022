package com.youtube_project.model.relationships.playlistshasvideos;

import com.youtube_project.model.entities.Playlist;
import com.youtube_project.model.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideosInPlaylistRepository extends JpaRepository<VideosInPlaylist,VideosInPlaylistKey> {

    void deleteAllByPlaylist(Playlist playlist);
}
