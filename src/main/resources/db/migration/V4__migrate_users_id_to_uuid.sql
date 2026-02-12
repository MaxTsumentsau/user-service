CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE users
    ADD COLUMN id_uuid UUID;

UPDATE users
SET id_uuid = gen_random_uuid();

ALTER TABLE users
    ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE users
    DROP CONSTRAINT users_pkey;

ALTER TABLE users
    DROP COLUMN id;

ALTER TABLE users
    RENAME COLUMN id_uuid TO id;

ALTER TABLE users
    ADD PRIMARY KEY (id);
