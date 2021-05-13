package com.students.recipesapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Base64;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String barcode;

    @Lob
    private byte[] image;

    @ManyToOne
    private UserEntity author;

    public void setImage(String imageInBase64) {
        if (imageInBase64 == null) return;
        image = Base64.getDecoder().decode(imageInBase64);
    }

    public String getImageBase64() {
        return Base64.getEncoder().encodeToString(image);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
