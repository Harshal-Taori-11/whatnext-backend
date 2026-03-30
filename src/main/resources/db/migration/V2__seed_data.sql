-- Insert default admin user
-- Password: admin123 (BCrypt encoded)
-- NOTE: Change this password immediately in production!
INSERT INTO users (email, username, password, full_name) VALUES
('admin@whatnext.com', 'admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'Admin User');

-- Insert default settings for admin user
INSERT INTO user_settings (user_id, auto_archive_days, auto_archive_enabled, default_priority, default_category, default_due_date, theme)
VALUES (1, 2, TRUE, 'none', 'other', 'none', 'auto');

-- Insert sample tasks for demo
INSERT INTO tasks (user_id, title, description, status, priority, category, due_date, position) VALUES
(1, 'Welcome to WhatNext!', 'This is your first task. Drag it to "In Progress" or "Done" to see it move!', 'new', 'none', 'personal', CURRENT_DATE, 0),
(1, 'Explore the All Tasks view', 'Click on "All Tasks" in the navigation to see the complete list view with advanced filters.', 'new', 'medium', 'personal', CURRENT_DATE + 1, 1),
(1, 'Try creating a note', 'Go to the Notes section and create your first quick note!', 'new', 'low', 'personal', CURRENT_DATE + 2, 2);

-- Insert sample tags
INSERT INTO tags (user_id, name, color) VALUES
(1, 'urgent', '#EF4444'),
(1, 'work', '#3B82F6'),
(1, 'personal', '#F97316'),
(1, 'home', '#10B981');

-- Link tags to tasks
INSERT INTO task_tags (task_id, tag_id) VALUES
(1, 3), -- Welcome task -> personal
(2, 3), -- Explore task -> personal
(3, 3); -- Note task -> personal
