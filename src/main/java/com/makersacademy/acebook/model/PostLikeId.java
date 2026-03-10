package com.makersacademy.acebook.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

// Since the primary id for the post_likes table is 2 keys (post_id and user_id)
// Will create a key class for the composite id

@Data
@Embeddable
public class PostLikeId implements Serializable {

    private Long userId;
    private Long postId;

}