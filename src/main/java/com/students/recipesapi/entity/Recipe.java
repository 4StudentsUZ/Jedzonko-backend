package com.students.recipesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.students.recipesapi.exception.Base64DecodingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Product> ingredients = new ArrayList<>();

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    private byte[] image;

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
