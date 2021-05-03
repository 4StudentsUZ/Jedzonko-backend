package com.students.recipesapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.postgresql.util.Base64;

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

    @ElementCollection
    private List<Long> ingredients;

    private byte[] image;

    public void setImage(String imageInBase64) {
        if (imageInBase64 == null) return;
        image = Base64.decode(imageInBase64);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

//    @OneToMany(mappedBy = "recipe")
//    private List<Ingredient> ingredient;
//
//    @OneToMany(mappedBy = "recipe")
//    private List<Rating> rating;

//    @ElementCollection
//    private List<String> tag;
//
//    @ElementCollection
//    private List<String> images;
}
