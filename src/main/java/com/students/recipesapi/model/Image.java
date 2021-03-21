package com.students.recipesapi.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private byte[] data;

    //    @ManyToMany(mappedBy = "product_image")
    @ManyToMany
    private Set<Product> products;
}
