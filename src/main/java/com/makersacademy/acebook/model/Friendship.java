package com.makersacademy.acebook.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "friendships")
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @Column(name = "status")
    private String status;

    public Friendship() {}

    public Friendship(Long requesterId, Long addresseeId) {
        FriendshipId friendshipId = new FriendshipId();
        friendshipId.setRequesterId(requesterId);
        friendshipId.setAddresseeId(addresseeId);
        this.id = friendshipId;
        this.status = "PENDING";
    }

}