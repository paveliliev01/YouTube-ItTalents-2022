package com.youtube_project.model.entities;

import com.youtube_project.model.relationships.playlistshasvideos.VideosInPlaylist;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column
    private LocalDateTime dateOfCreation;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isPrivate;
    @ManyToMany(fetch =FetchType.LAZY)
    @JoinTable(name = "videos_in_playlists",
            joinColumns = {@JoinColumn(name = "playlist_id")},
            inverseJoinColumns = {@JoinColumn(name = "video_id")})
    private Set<Video> videos = new HashSet<>();

}
