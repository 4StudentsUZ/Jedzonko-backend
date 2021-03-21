package com.students.recipesapi.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    private User author;

    @ManyToMany
    private Set<Image> images;

    @ManyToMany
    private Set<Tag> tags;
}
