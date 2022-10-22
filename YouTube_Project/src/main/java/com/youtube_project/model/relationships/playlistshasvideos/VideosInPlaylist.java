package com.youtube_project.model.relationships.playlistshasvideos;

import com.youtube_project.model.entities.Playlist;
import com.youtube_project.model.entities.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "videos_in_playlists")
@Data
@EqualsAndHashCode
public class VideosInPlaylist {

    @EmbeddedId
    private VideosInPlaylistKey videosInPlaylistKey;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    @JoinColumn(name = "video_id")
    private Video video;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;
    @Column(name ="added_to_playlist_date")
    private LocalDateTime dateOfAdding;

}
