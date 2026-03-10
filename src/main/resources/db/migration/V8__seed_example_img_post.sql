INSERT INTO posts (content, user_id, created_at, image_url)
VALUES ('Alice here, here is a photo!.',
        (SELECT id FROM users WHERE username = 'alice'),
        now() - interval '1 day',
        'https://res.cloudinary.com/acebook-sapphire/image/upload/v1773159971/pexels-muhammad-yunus-2160062343-36489621_wli4rs.jpg');
