-- Database initialization script for tasks table

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration INTEGER
);

-- Create index on title for faster lookups
CREATE INDEX idx_tasks_title ON tasks(title);

-- Insert sample data (optional)
INSERT INTO tasks (title, description, duration) VALUES
    ('Setup Database', 'Create PostgreSQL database with tasks table', 30),
    ('Write Documentation', 'Document the database schema and setup process', 45);
