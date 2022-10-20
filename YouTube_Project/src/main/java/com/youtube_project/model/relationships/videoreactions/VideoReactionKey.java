package com.youtube_project.model.relationships.videoreactions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class VideoReactionKey implements Serializable {

        @Column(name = "video_id")
        private Long videoId;

        @Column(name = "user_id")
        private Long userId;

}
