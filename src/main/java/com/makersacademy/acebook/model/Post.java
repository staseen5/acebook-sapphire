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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy="post")
    private List<Comment> comments;
  
    @Column(name = "image_url")
    private String imageUrl;

    public Post() {}

    public Post(String content, User user) {
        this.content = content;
        this.user = user;
        this.createdAt = ZonedDateTime.now();
    }
}
