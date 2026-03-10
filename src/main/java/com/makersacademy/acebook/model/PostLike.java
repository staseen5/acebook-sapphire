package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post_likes")
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    public PostLike() {}

    public PostLike(Long userId, Long postId) {
        PostLikeId likeId = new PostLikeId();
        likeId.setUserId(userId);
        likeId.setPostId(postId);
        this.id = likeId;
    }

}