package com.students.recipesapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String barcode;

    @ManyToOne
    private Image image;

    @OneToMany(mappedBy = "product")
    private List<Ingredient> recipe;
}
