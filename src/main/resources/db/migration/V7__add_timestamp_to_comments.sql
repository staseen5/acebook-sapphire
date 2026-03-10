ALTER TABLE comments ADD COLUMN commented_on timestamptz NOT NULL DEFAULT now();

UPDATE comments SET commented_on = now() - interval '1 day' WHERE id = 1;
UPDATE comments SET commented_on = now() - interval '1 hour' WHERE id = 2;
UPDATE comments SET commented_on = now() - interval '1 hour' WHERE id = 3;