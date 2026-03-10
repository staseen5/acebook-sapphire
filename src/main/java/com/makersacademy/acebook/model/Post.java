package com.makersacademy.acebook.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

import java.util.*;

@Data
@Entity
@Getter
@Setter
@Table(name = "POSTS")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy="post")
    private List<Comment> comments;

    public Post() {}

    public Post(String content, Long userId) {
        this.content = content;
        this.userId = userId;
        this.createdAt = ZonedDateTime.now();
    }
}
