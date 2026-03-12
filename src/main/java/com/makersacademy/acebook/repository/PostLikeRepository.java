package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.PostLike;
import com.makersacademy.acebook.model.PostLikeId;
import org.springframework.data.repository.CrudRepository;

public interface PostLikeRepository extends CrudRepository<PostLike, PostLikeId> {

    long countByIdPostId(Long postId);
    boolean existsByIdUserIdAndIdPostId(Long userId, Long postId);

}