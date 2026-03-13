package com.makersacademy.acebook.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class PostTagId implements Serializable {
    private Long postId;
    private Long userId;
}
