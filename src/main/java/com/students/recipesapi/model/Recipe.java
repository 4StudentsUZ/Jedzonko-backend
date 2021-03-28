package com.students.recipesapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Lob
    private String description;

    @ManyToOne
    private UserEntity author;

    @ManyToMany
    private Set<Image> image;

    @OneToMany(mappedBy = "recipe")
    private List<Ingredient> ingredient;

    @OneToMany(mappedBy = "recipe")
    private List<Rating> rating;

    @ElementCollection
    private List<String> tag;
}
