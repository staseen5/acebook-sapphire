package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.PostTag;
import com.makersacademy.acebook.model.PostTagId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostTagRepository extends CrudRepository<PostTag, PostTagId> {
    List<PostTag> findByIdUserId(Long userId);

    boolean existsByIdPostIdAndIdUserId(Long postId, Long userId);
}
