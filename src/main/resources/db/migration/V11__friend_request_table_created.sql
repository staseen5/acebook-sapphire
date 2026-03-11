--  Same idea with liking posts, made a db where there are some states
--  Default is pending, then can either be accepted or blocked
--  When user send a fr, it will make a record with "pending"
--  If they accept it will change state, if they decline it will delete the record
--  Will also add BLOCKED as a state, so we can support blocking users later

CREATE TABLE friendships (
                             requester_id bigint NOT NULL REFERENCES users(id),
                             addressee_id bigint NOT NULL REFERENCES users(id),
                             status varchar(10) NOT NULL DEFAULT 'PENDING',
                             PRIMARY KEY (requester_id, addressee_id)
);