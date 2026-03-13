package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.id.requesterId = :userId OR f.id.addresseeId = :userId) AND f.status = :status")
    List<Friendship> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    List<Friendship> findByIdAddresseeIdAndStatus(Long addresseeId, String status);

    List<Friendship> findByIdRequesterIdAndStatus(Long requesterId, String status);

    Optional<Friendship> findByIdRequesterIdAndIdAddresseeId(Long requesterId, Long addresseeId);

    boolean existsByIdRequesterIdAndIdAddresseeIdAndStatus(Long requesterId, Long addresseeId, String status);
}