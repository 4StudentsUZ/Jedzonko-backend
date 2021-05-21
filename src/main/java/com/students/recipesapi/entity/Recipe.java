package com.students.recipesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.students.recipesapi.exception.Base64DecodingException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String description;

    @ManyToOne
    private UserEntity author;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER)
    private Set<RecipeIngredient> ingredients;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tags = new LinkedHashSet<>();

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] image;

    private String creationDate;
    private String modificationDate;

    private Double rating = 0.0;

    public void setImage(String imageInBase64) {
        if (imageInBase64 == null) return;
        try {
            image = Base64.getDecoder().decode(imageInBase64);
        } catch (Exception e) {
            throw new Base64DecodingException("Failed to decode Base64 string.");
        }
    }

    @JsonIgnore
    public String getImageBase64() {
        return Base64.getEncoder().encodeToString(image);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
