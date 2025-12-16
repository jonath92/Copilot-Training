-- Database initialization script for Task Estimation Application
-- Creates the tasks table with required fields

-- Create custom domain for non-negative duration
CREATE DOMAIN non_negative_int AS INTEGER
    CHECK (VALUE >= 0);

-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    duration non_negative_int NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT title_not_empty CHECK (length(trim(title)) > 0)
);

-- Add comments for documentation
COMMENT ON TABLE tasks IS 'Stores task estimation data';
COMMENT ON COLUMN tasks.id IS 'Unique identifier for the task';
COMMENT ON COLUMN tasks.title IS 'Title of the task';
COMMENT ON COLUMN tasks.description IS 'Detailed description of the task';
COMMENT ON COLUMN tasks.duration IS 'Estimated time to complete the task in minutes';
COMMENT ON COLUMN tasks.created_at IS 'Timestamp when the task was created';
COMMENT ON COLUMN tasks.updated_at IS 'Timestamp when the task was last updated';

-- Create index on title for faster searches
CREATE INDEX IF NOT EXISTS idx_tasks_title ON tasks(title);

-- Trigger function to automatically update updated_at
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update updated_at on row changes
CREATE TRIGGER update_tasks_modified_time
    BEFORE UPDATE ON tasks
    FOR EACH ROW
    WHEN (OLD.* IS DISTINCT FROM NEW.*)
    EXECUTE FUNCTION update_modified_time();
