package com.youtube_project.model.relationships.playlistshasvideos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class VideosInPlaylistKey implements Serializable {

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "playlist_id")
    private Long playlistId;

}
