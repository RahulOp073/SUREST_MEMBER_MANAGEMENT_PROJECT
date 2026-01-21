
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id UUID NOT NULL,
                                          role_id UUID NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES user_table(id),
    CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_id) REFERENCES role(id)
    );

INSERT INTO user_roles (user_id, role_id)
SELECT id AS user_id, role_id
FROM user_table
WHERE role_id IS NOT NULL;

ALTER TABLE user_table DROP COLUMN IF EXISTS role_id;
