package com.students.recipesapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.postgresql.util.Base64;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String barcode;
    private byte[] image;

    @ManyToOne
    private UserEntity author;

    public void setImage(String imageInBase64) {
        if (imageInBase64 == null) return;
        image = Base64.decode(imageInBase64);
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
