package com.youtube_project.model.entities;

import com.sun.istack.NotNull;
import com.youtube_project.model.relationships.videoreactions.VideoReaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Entity
@Component
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotNull
    private String firstName;
    @Column
    @NotNull
    private String lastName;
    @Column
    @NotNull
    private String password;
    @Column
    @NotNull
    private String email;
    @Column
    @NotNull
    private String phoneNumber;
    @Column
    @NotNull
    private LocalDate dateOfBirth;
    @Column
    private int roleId;
    @Column
    @NotNull
    private int genderId;
    @Column
    private String profilePhoto;
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isVerified;
    @Column
    private boolean isAdmin;
    @Column(name = "additional_info")
    private String additionalInfo;
    @Column
    private String backgroundImage;
    @OneToMany(mappedBy = "owner")
    private Set<Video> videosUploaded;
    @OneToMany(mappedBy = "user")
    private List<VideoReaction> reactedVideos;
    @ManyToMany
    @JoinTable(
            name = "users_watched_videos",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private List<Video> watchedVideos;
    @ManyToMany(mappedBy = "subscriptions")
    private List<User> followers;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "subscribers",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscribed_to")})
    private Set<User> subscriptions;
    @OneToMany(mappedBy = "owner")
    private Set<Playlist> playlists;
    @ManyToMany
    @JoinTable(
            name = "users_followed_categories",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> followedCategories;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isDeleted;


}
