package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;

import java.time.ZonedDateTime;

@Data
@Entity
@Getter
@Setter
@Table (name="COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String body;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @Column(name = "commented_on")
    private ZonedDateTime commentedOn;

    public Comment() {}

    public Comment(String body, User user, Post post) {
        this.body = body;
        this.user = user;
        this.post = post;
        this.commentedOn = ZonedDateTime.now();
    }
}