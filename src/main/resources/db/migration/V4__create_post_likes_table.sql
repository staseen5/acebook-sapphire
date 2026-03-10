-- Idea is that when a user likes a post it will show up here with the userid and post id
-- If they unlike it will remove the record
-- If they try like again, it would do nothing since the record already exists
-- Can count number of likes by just counting number of records for the post

CREATE TABLE post_likes (
                            user_id bigint NOT NULL REFERENCES users(id),
                            post_id bigint NOT NULL REFERENCES posts(id),
                            PRIMARY KEY (user_id, post_id)
);