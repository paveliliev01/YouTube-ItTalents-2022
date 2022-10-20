package com.youtube_project.model.relationships.videoreaction;

import com.youtube_project.model.entities.User;
import com.youtube_project.model.entities.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name = "videos_have_reactions")
@Data
@EqualsAndHashCode
public class VideoReaction {

    @EmbeddedId
    private VideoReactionKey id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    @JoinColumn(name = "video_id")
    private Video video;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name ="reaction")
    private char reaction;


}
