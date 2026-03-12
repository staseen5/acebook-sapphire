CREATE TABLE post_tags (
    post_id bigint NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id bigint NOT NULL REFERENCES users(id) ON DELETE  CASCADE,
    PRIMARY KEY (post_id, user_id)
);