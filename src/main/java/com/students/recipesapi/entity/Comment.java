package com.students.recipesapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String creationDate;
    private String modificationDate;

    @JsonIgnore
    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private UserEntity author;
}
