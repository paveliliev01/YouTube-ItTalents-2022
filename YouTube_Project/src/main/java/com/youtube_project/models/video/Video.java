package com.youtube_project.models.video;

import com.youtube_project.models.relationships.videoreaction.VideoReaction;
import com.youtube_project.models.user.User;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Component
@Data
@Entity
@Table(name = "videos")
public class Video {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String title;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @Column
    private LocalDate dateOfUpload;
    @Column(name = "video_url")
    private String videoURL;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isPrivate;
    @OneToMany(mappedBy = "video")
    Set<VideoReaction> ratings;


}
