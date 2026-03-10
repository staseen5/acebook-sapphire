package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;

<<<<<<< HEAD
import java.time.ZonedDateTime;

=======
>>>>>>> 098e7b4 (Adds comment (read-only))
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

<<<<<<< HEAD
    @Column(name = "commented_on")
    private ZonedDateTime commentedOn;

=======
>>>>>>> 098e7b4 (Adds comment (read-only))
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;
}