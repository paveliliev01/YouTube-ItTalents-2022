package com.youtube_project.model.entities;

import com.youtube_project.model.relationships.commentsreactions.CommentReaction;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @OneToMany(mappedBy = "comment")
    private List<CommentReaction> reactions;
    @OneToMany
    @JoinColumn(name = "parent_id")
    private List<Comment> subComments;


}
