package com.youtube_project.model.entities;

import com.youtube_project.model.relationships.videoreactions.VideoReaction;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
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
    private List<VideoReaction> videoReactions;
    @OneToMany(mappedBy = "video")
    private List<Comment> comments;
    @ManyToMany(mappedBy = "watchedVideos")
    private List<User> viewers;
    @ManyToMany
    @JoinTable(
            name = "videos_in_categories",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categoriesContainingVideo;

}
