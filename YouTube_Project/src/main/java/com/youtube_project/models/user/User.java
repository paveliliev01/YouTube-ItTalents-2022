package com.youtube_project.models.user;

import com.sun.istack.NotNull;
import com.youtube_project.models.relationships.videoreaction.VideoReaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
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
    @Column(name = "additional_info")
    private String additionalInformation;
    @Column
    private String backgroundImage;
    @OneToMany(mappedBy = "user")
    private Set<VideoReaction> reactions;
    @ManyToMany(mappedBy = "followers")
    private Set<User> subscribedTo = new HashSet<>();
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "subscribers",
            joinColumns = {@JoinColumn(name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name = "subscribed_to")})
    private Set<User> followers = new HashSet<>();

}
