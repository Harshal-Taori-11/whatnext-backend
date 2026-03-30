-- V3: Add new features - Task codes, User profile, OTP verification, Theme colors

-- Add new columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS country_code VARCHAR(5) DEFAULT '+91';
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS task_code VARCHAR(5) UNIQUE;

-- Add new columns to tasks table
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS task_code VARCHAR(5);
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS task_number INTEGER;

-- Modify tasks status enum to include 'removed'
ALTER TABLE tasks ALTER COLUMN status TYPE VARCHAR(20);
-- Now we can store: 'new', 'in_progress', 'done', 'archived', 'removed'

-- Add theme_color to user_settings
ALTER TABLE user_settings ADD COLUMN IF NOT EXISTS theme_color VARCHAR(20) DEFAULT 'blue';
-- Options: 'blue', 'pink', 'green', 'yellow', 'black', 'red'

-- Create OTP verification table
CREATE TABLE IF NOT EXISTS otp_verification (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_expires_at (expires_at)
);

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_tasks_task_code ON tasks(task_code);
CREATE INDEX IF NOT EXISTS idx_tasks_user_status ON tasks(user_id, status);
CREATE INDEX IF NOT EXISTS idx_users_task_code ON users(task_code);
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);

-- Update existing admin user with task code
UPDATE users SET task_code = 'ADMIN' WHERE username = 'admin';

-- Generate task codes for existing tasks
DO $$
DECLARE
    task_rec RECORD;
    user_task_code VARCHAR(5);
    task_counter INTEGER;
BEGIN
    FOR task_rec IN SELECT t.id, t.user_id, u.username
                    FROM tasks t
                    JOIN users u ON t.user_id = u.id
    LOOP
        -- Get or create task code for user
        SELECT task_code INTO user_task_code FROM users WHERE id = task_rec.user_id;

        -- Count existing tasks for this user
        SELECT COUNT(*) INTO task_counter FROM tasks WHERE user_id = task_rec.user_id AND id <= task_rec.id;

        -- Update task with code and number
        UPDATE tasks
        SET task_code = user_task_code,
            task_number = task_counter
        WHERE id = task_rec.id;
    END LOOP;
END $$;
