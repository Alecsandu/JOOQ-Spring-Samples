CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    hashed_password VARCHAR(128) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS links (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    original_link VARCHAR(255) NOT NULL,
    generated_link VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL
);

-- Constraints

ALTER TABLE links
ADD CONSTRAINT fk_links_users FOREIGN KEY (user_id)
REFERENCES users(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

--- Triggers

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp_users
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();

--- INSERT DATA

INSERT INTO users (id, email, hashed_password) VALUES
    ('ce030aac-6136-4661-a4eb-4a5d0ff52eb1', 'test@test.com', ' ')
;

INSERT INTO links (original_link, generated_link, user_id) VALUES
    ('https://www.phind.com/', 'https://www.phind.com/',    'ce030aac-6136-4661-a4eb-4a5d0ff52eb1'),
    ('https://www.phind.com/', 'https://www.phind.com/wow', 'ce030aac-6136-4661-a4eb-4a5d0ff52eb1')
;
