package com.makersacademy.acebook.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "post_tags")
public class PostTag {

    @EmbeddedId
    private PostTagId id;

    public PostTag() {}

    public PostTag(Long postId, Long userId){
        PostTagId tagId = new PostTagId();
        tagId.setPostId(postId);
        tagId.setUserId(userId);
        this.id = tagId;
    }
}
