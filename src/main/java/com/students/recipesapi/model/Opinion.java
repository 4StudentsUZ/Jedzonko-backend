package com.students.recipesapi.model;

import javax.persistence.*;

@Entity
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;
    private int rating;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private User author;
}
