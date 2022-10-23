package com.youtube_project.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column(name = "date_of_creation")
    private LocalDate creationDate;
    @ManyToMany(mappedBy = "followedCategories")
    private Set<User> followers;
    @ManyToMany(mappedBy = "categoriesContainingVideo")
    private Set<Video> videosInCategory;
}
