package com.students.recipesapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String content;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private UserEntity author;
}
