-- Create the tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration INTEGER -- Duration in minutes
);

-- Insert some sample data
INSERT INTO tasks (title, description, duration) VALUES
    ('Setup project', 'Initialize the project repository and structure', 60),
    ('Write documentation', 'Create README and API documentation', 120),
    ('Code review', 'Review pull requests from team members', 45);
