package com.students.recipesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.students.recipesapi.exception.Base64DecodingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Base64;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String barcode;

    @Basic(fetch = FetchType.EAGER)
    @Lob
    private byte[] image;

    @ManyToOne
    private UserEntity author;

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
}
