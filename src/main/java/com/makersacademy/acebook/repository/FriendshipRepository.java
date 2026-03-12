package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.FriendshipId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


// Added methods to allow to look for all incoming and outing friend requests
// Also added a method to see all friends and one to see all blocked users (for later)


public interface FriendshipRepository extends CrudRepository<Friendship, FriendshipId> {

    List<Friendship> findByIdAddresseeIdAndStatus(Long addresseeId, String status);

    List<Friendship> findByIdRequesterIdAndStatus(Long requesterId, String status);

    Optional<Friendship> findByIdRequesterIdAndIdAddresseeId(Long requesterId, Long addresseeId);

    List<Friendship> findByIdRequesterIdOrIdAddresseeIdAndStatus(Long requesterId, Long addresseeId, String status);

}