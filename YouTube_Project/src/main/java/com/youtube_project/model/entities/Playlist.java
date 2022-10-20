package com.youtube_project.model.entities;

import com.youtube_project.model.entities.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private User user;
    @Column
    private LocalDateTime dateOfCreation;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isPrivate;
}
