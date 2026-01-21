CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS role (
                                    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS user_table (
                                          id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       UUID,
    CONSTRAINT fk_user_role
    FOREIGN KEY (role_id) REFERENCES role(id)
    );
CREATE TABLE IF NOT EXISTS member (
                                      id            UUID PRIMARY KEY,
                                      first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    email         VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth DATE
    );

INSERT INTO role (name)
VALUES ('ADMIN')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO role (name)
VALUES ('USER')
    ON CONFLICT (name) DO NOTHING;
INSERT INTO user_table (username, password_hash)
VALUES (
           'admin',
           '$2a$10$1p8n3wrkgRbjiaG/eUIrbOTlAVNXBzpDrR4RQxAQ9GAbYeWOa8g3u'
       )
    ON CONFLICT (username) DO NOTHING;

INSERT INTO user_table (username, password_hash)
VALUES (
           'user',
           '$2a$10$6W5l/8XQSr1Nmw5Y/c1ypebflB65fcxdtv8NIMmROgaF.1QlrdaqS'
       )
    ON CONFLICT (username) DO NOTHING;

UPDATE user_table
SET role_id = (SELECT id FROM role WHERE name = 'ADMIN')
WHERE username = 'admin';

UPDATE user_table
SET role_id = (SELECT id FROM role WHERE name = 'USER')
WHERE username = 'user';