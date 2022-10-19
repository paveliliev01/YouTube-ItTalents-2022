package com.youtube_project.models.comment;

import com.youtube_project.models.user.User;
import com.youtube_project.models.video.Video;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String text;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @Column(name = "date_of_creation")
    private LocalDateTime dateOfCreation;

}
