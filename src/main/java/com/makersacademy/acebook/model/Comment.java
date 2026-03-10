package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;

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

    @Column(name="user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;
}