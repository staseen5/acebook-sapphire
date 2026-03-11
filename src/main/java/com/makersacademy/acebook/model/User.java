package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

@Data
@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean enabled;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    public User() {
        this.enabled = TRUE;
    }

    public User(String email) {
        this.email = email;
        this.enabled = TRUE;
    }

    public User(String email, boolean enabled) {
        this.email = email;
        this.enabled = enabled;
    }

    public String getProfilePictureUrl() {
        return Objects.requireNonNullElse(this.profilePictureUrl, "https://static.vecteezy.com/system/resources/thumbnails/036/594/092/small/man-empty-avatar-photo-placeholder-for-social-networks-resumes-forums-and-dating-sites-male-and-female-no-photo-images-for-unfilled-user-profile-free-vector.jpg");
    }
}
