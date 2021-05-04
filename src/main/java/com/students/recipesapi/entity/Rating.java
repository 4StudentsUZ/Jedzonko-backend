package com.students.recipesapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private Recipe recipe;

    @Column
    private int rating;
}
