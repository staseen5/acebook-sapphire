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

    @ManyToOne
    @MapsId("requesterId")
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    @MapsId("addresseeId")
    @JoinColumn(name = "addressee_id")
    private User addressee;

    public Friendship() {}

    public Friendship(Long requesterId, Long addresseeId) {
        FriendshipId friendshipId = new FriendshipId();
        friendshipId.setRequesterId(requesterId);
        friendshipId.setAddresseeId(addresseeId);
        this.id = friendshipId;
        this.status = "PENDING";
    }

}