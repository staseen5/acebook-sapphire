package com.makersacademy.acebook.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class FriendshipId implements Serializable {

    private Long requesterId;
    private Long addresseeId;

}