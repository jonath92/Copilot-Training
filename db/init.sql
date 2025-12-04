-- Initialize the tasks database
CREATE DATABASE IF NOT EXISTS taskdb;

-- Use the tasks database
\c taskdb;

-- Enable PostgreSQL extensions for advanced features
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";    -- UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";     -- Cryptographic functions
CREATE EXTENSION IF NOT EXISTS "pg_trgm";      -- Trigram matching for fuzzy search
CREATE EXTENSION IF NOT EXISTS "btree_gin";    -- GIN indexes for btree types

-- Create custom domains for data validation
CREATE DOMAIN positive_duration AS INTEGER 
CHECK (VALUE > 0 AND VALUE <= 10080); -- Max 1 week in minutes

CREATE DOMAIN task_title AS TEXT
CHECK (char_length(VALUE) >= 3 AND char_length(VALUE) <= 255 AND VALUE ~ '^[^<>]*$');

-- Create ENUM for task status
CREATE TYPE task_status AS ENUM ('pending', 'in_progress', 'completed', 'cancelled', 'archived');

-- Create ENUM for task priority
CREATE TYPE task_priority AS ENUM ('low', 'medium', 'high', 'urgent');

-- Create the optimized tasks table with PostgreSQL advanced features
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title task_title NOT NULL,
    description TEXT,
    duration positive_duration, -- Duration in minutes with validation
    status task_status DEFAULT 'pending',
    priority task_priority DEFAULT 'medium',
    tags TEXT[] DEFAULT '{}', -- PostgreSQL array for flexible tagging
    metadata JSONB DEFAULT '{}', -- Flexible metadata storage
    estimated_start TIMESTAMPTZ,
    estimated_end TIMESTAMPTZ,
    actual_start TIMESTAMPTZ,
    actual_end TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by TEXT DEFAULT current_user,
    
    -- Constraints
    CONSTRAINT valid_time_range CHECK (
        (estimated_start IS NULL OR estimated_end IS NULL OR estimated_start < estimated_end) AND
        (actual_start IS NULL OR actual_end IS NULL OR actual_start < actual_end)
    ),
    CONSTRAINT valid_duration_match CHECK (
        duration IS NULL OR 
        estimated_start IS NULL OR 
        estimated_end IS NULL OR 
        duration = EXTRACT(EPOCH FROM (estimated_end - estimated_start))/60
    )
);

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_tasks_updated_at 
    BEFORE UPDATE ON tasks 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Insert optimized sample data with PostgreSQL features
INSERT INTO tasks (title, description, duration, status, priority, tags, metadata) VALUES
    ('Setup Development Environment', 'Install and configure development tools', 120, 'completed', 'high', 
     ARRAY['setup', 'development', 'tools'], 
     '{"complexity": "medium", "requires_admin": true, "os": ["linux", "macos", "windows"]}'),
    ('Create Database Schema', 'Design and implement database structure', 90, 'completed', 'high',
     ARRAY['database', 'schema', 'postgresql'], 
     '{"complexity": "high", "tables": 5, "constraints": 12}'),
    ('Build API Endpoints', 'Develop REST API for task management', 180, 'in_progress', 'high',
     ARRAY['api', 'rest', 'backend'], 
     '{"complexity": "high", "endpoints": 8, "authentication": "jwt"}'),
    ('Frontend Implementation', 'Create user interface for task management', 240, 'pending', 'medium',
     ARRAY['frontend', 'ui', 'react'], 
     '{"complexity": "medium", "components": 15, "responsive": true}'),
    ('Write Unit Tests', 'Comprehensive test coverage for all components', 180, 'pending', 'medium',
     ARRAY['testing', 'quality', 'coverage'], 
     '{"complexity": "medium", "coverage_target": "80%", "frameworks": ["jest", "cypress"]}');

-- Advanced PostgreSQL indexes for optimal performance
-- Composite index for common queries (status + priority)
CREATE INDEX idx_tasks_status_priority ON tasks(status, priority);

-- Partial index for active tasks only
CREATE INDEX idx_tasks_active ON tasks(created_at, priority) 
WHERE status IN ('pending', 'in_progress');

-- GIN index for array operations on tags
CREATE INDEX idx_tasks_tags_gin ON tasks USING gin(tags);

-- GIN index for JSONB metadata queries
CREATE INDEX idx_tasks_metadata_gin ON tasks USING gin(metadata);

-- Expression index for case-insensitive title searches
CREATE INDEX idx_tasks_title_lower ON tasks(lower(title));

-- GiST index for trigram similarity searches on title and description
CREATE INDEX idx_tasks_title_trgm ON tasks USING gist(title gist_trgm_ops);
CREATE INDEX idx_tasks_description_trgm ON tasks USING gist(description gist_trgm_ops);

-- Covering index to avoid table lookups for common queries
CREATE INDEX idx_tasks_covering ON tasks(status, priority) 
INCLUDE (title, duration, created_at);

-- Time-based indexes for reporting and analytics
CREATE INDEX idx_tasks_created_at_brin ON tasks USING brin(created_at);
CREATE INDEX idx_tasks_time_range ON tasks(estimated_start, estimated_end) 
WHERE estimated_start IS NOT NULL;

-- Create a view for task analytics with window functions
CREATE VIEW task_analytics AS
SELECT 
    id,
    title,
    status,
    priority,
    duration,
    created_at,
    -- Running statistics
    AVG(duration) OVER (ORDER BY created_at ROWS BETWEEN 4 PRECEDING AND CURRENT ROW) as avg_recent_duration,
    ROW_NUMBER() OVER (PARTITION BY status ORDER BY priority DESC, created_at) as status_rank,
    -- Time analysis
    CASE 
        WHEN actual_start IS NOT NULL AND actual_end IS NOT NULL 
        THEN EXTRACT(EPOCH FROM (actual_end - actual_start))/60 
    END as actual_duration_minutes,
    -- Completion rate by priority
    COUNT(*) FILTER (WHERE status = 'completed') OVER (PARTITION BY priority) * 100.0 / 
    COUNT(*) OVER (PARTITION BY priority) as priority_completion_rate
FROM tasks;

-- Create function for fuzzy search across tasks
CREATE OR REPLACE FUNCTION search_tasks(search_term TEXT)
RETURNS TABLE(
    task_id UUID,
    title TEXT,
    description TEXT,
    similarity_score REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        t.id,
        t.title,
        t.description,
        GREATEST(
            similarity(t.title, search_term),
            COALESCE(similarity(t.description, search_term), 0)
        ) as similarity_score
    FROM tasks t
    WHERE 
        t.title % search_term OR 
        (t.description IS NOT NULL AND t.description % search_term) OR
        EXISTS (SELECT 1 FROM unnest(t.tags) tag WHERE tag % search_term)
    ORDER BY similarity_score DESC, t.created_at DESC;
END;
$$ LANGUAGE plpgsql;

-- Grant permissions to the task user
GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO taskuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO taskuser;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO taskuser;
GRANT USAGE ON ALL TYPES IN SCHEMA public TO taskuser;