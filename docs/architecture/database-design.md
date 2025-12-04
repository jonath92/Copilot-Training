# Database Design

The Task Management Application uses PostgreSQL with advanced features for robust data management, performance optimization, and data integrity.

## Schema Overview

The database schema is designed around the core `tasks` table with supporting custom types, views, and functions for a comprehensive task management system.

## Custom Types

### Domains

#### task_title
Validates task titles with business rules:
```sql
CREATE DOMAIN task_title AS VARCHAR(255)
  CHECK (VALUE ~ '^[^\s]' AND VALUE ~ '[^\s]$' AND LENGTH(VALUE) BETWEEN 3 AND 255);
```

**Constraints**:
- Length: 3-255 characters
- Cannot start or end with whitespace
- Must contain meaningful content

#### positive_duration
Ensures duration is within acceptable limits:
```sql
CREATE DOMAIN positive_duration AS INTEGER
  CHECK (VALUE BETWEEN 1 AND 10080);
```

**Constraints**:
- Minimum: 1 minute
- Maximum: 10,080 minutes (1 week)

### Enumerations

#### task_status
Lifecycle states for tasks:
```sql
CREATE TYPE task_status AS ENUM (
  'pending',
  'in_progress',
  'completed',
  'cancelled',
  'archived'
);
```

**State Transitions**:
- `pending` → `in_progress` (task started)
- `in_progress` → `completed` (task finished)
- `in_progress` → `cancelled` (task abandoned)
- `completed` | `cancelled` → `archived` (moved to archive)

#### task_priority
Priority levels for task scheduling:
```sql
CREATE TYPE task_priority AS ENUM (
  'low',
  'medium',
  'high',
  'urgent'
);
```

## Main Table: tasks

### Schema Definition

```sql
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title task_title NOT NULL,
    description TEXT,
    duration positive_duration,
    status task_status NOT NULL DEFAULT 'pending',
    priority task_priority NOT NULL DEFAULT 'medium',
    tags TEXT[],
    metadata JSONB DEFAULT '{}'::jsonb,
    estimated_start TIMESTAMPTZ,
    estimated_end TIMESTAMPTZ,
    actual_start TIMESTAMPTZ,
    actual_end TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by TEXT,
    
    CONSTRAINT valid_estimated_time_range 
      CHECK (estimated_end IS NULL OR estimated_start IS NULL OR estimated_end > estimated_start),
    CONSTRAINT valid_actual_time_range 
      CHECK (actual_end IS NULL OR actual_start IS NULL OR actual_end > actual_start),
    CONSTRAINT duration_matches_time 
      CHECK (
        (estimated_start IS NULL OR estimated_end IS NULL OR duration IS NULL) OR
        EXTRACT(EPOCH FROM (estimated_end - estimated_start)) / 60 = duration
      )
);
```

### Column Reference

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| `id` | UUID | No | `uuid_generate_v4()` | Unique identifier |
| `title` | task_title | No | - | Task title (3-255 chars) |
| `description` | TEXT | Yes | - | Detailed description |
| `duration` | positive_duration | Yes | - | Duration in minutes (1-10080) |
| `status` | task_status | No | 'pending' | Current task status |
| `priority` | task_priority | No | 'medium' | Task priority level |
| `tags` | TEXT[] | Yes | - | Array of categorization tags |
| `metadata` | JSONB | Yes | '{}' | Flexible JSON metadata |
| `estimated_start` | TIMESTAMPTZ | Yes | - | Estimated start time |
| `estimated_end` | TIMESTAMPTZ | Yes | - | Estimated completion time |
| `actual_start` | TIMESTAMPTZ | Yes | - | Actual start time |
| `actual_end` | TIMESTAMPTZ | Yes | - | Actual completion time |
| `created_at` | TIMESTAMPTZ | No | NOW() | Creation timestamp |
| `updated_at` | TIMESTAMPTZ | No | NOW() | Last update timestamp |
| `created_by` | TEXT | Yes | - | User who created the task |

### Constraints

#### Data Integrity
- **valid_estimated_time_range**: Ensures estimated end is after start
- **valid_actual_time_range**: Ensures actual end is after start
- **duration_matches_time**: Validates duration equals time difference

## Indexes

### Index Strategy

The database uses multiple index types for optimal query performance:

#### Composite Index (B-tree)
```sql
CREATE INDEX idx_tasks_status_priority_created ON tasks(status, priority, created_at DESC);
```
**Purpose**: Optimize filtering and sorting by status, priority, and creation date

#### Partial Index (B-tree)
```sql
CREATE INDEX idx_tasks_active_by_priority ON tasks(priority, estimated_start)
  WHERE status IN ('pending', 'in_progress');
```
**Purpose**: Fast queries for active tasks only

#### GIN Index (Array)
```sql
CREATE INDEX idx_tasks_tags ON tasks USING GIN(tags);
```
**Purpose**: Efficient array containment queries (`tags @> ARRAY['tag1']`)

#### GIN Index (JSONB)
```sql
CREATE INDEX idx_tasks_metadata ON tasks USING GIN(metadata jsonb_path_ops);
```
**Purpose**: Fast JSONB containment queries

#### GiST Index (Trigram)
```sql
CREATE INDEX idx_tasks_title_trgm ON tasks USING GiST(title gist_trgm_ops);
```
**Purpose**: Fuzzy text search with similarity matching

#### BRIN Index (Time-series)
```sql
CREATE INDEX idx_tasks_created_at_brin ON tasks USING BRIN(created_at);
```
**Purpose**: Efficient range queries on creation timestamps

#### Covering Index
```sql
CREATE INDEX idx_tasks_status_covering ON tasks(status)
  INCLUDE (id, title, priority, created_at);
```
**Purpose**: Index-only scans for common queries

## Functions

### update_updated_at_column()

Trigger function to automatically update the `updated_at` timestamp:

```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

**Trigger**:
```sql
CREATE TRIGGER update_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

### search_tasks(search_term)

Fuzzy search function using trigram similarity:

```sql
CREATE OR REPLACE FUNCTION search_tasks(search_term TEXT)
RETURNS TABLE (
    task_id UUID,
    task_title VARCHAR(255),
    similarity_score REAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        t.id,
        t.title::VARCHAR(255),
        similarity(t.title::text, search_term) AS sim_score
    FROM tasks t
    WHERE t.title::text % search_term
    ORDER BY sim_score DESC, t.created_at DESC;
END;
$$ LANGUAGE plpgsql;
```

**Usage**:
```sql
SELECT * FROM search_tasks('implement feature');
```

## Views

### task_analytics

Provides aggregated analytics and statistics:

```sql
CREATE VIEW task_analytics AS
SELECT 
    status,
    COUNT(*) as total_tasks,
    COUNT(*) FILTER (WHERE actual_end IS NOT NULL) as completed_count,
    AVG(EXTRACT(EPOCH FROM (actual_end - actual_start))/60) as avg_actual_duration,
    AVG(duration) as avg_estimated_duration
FROM tasks
GROUP BY status;
```

**Columns**:
- `status` - Task status
- `total_tasks` - Count of tasks in this status
- `completed_count` - Count with actual completion
- `avg_actual_duration` - Average actual duration (minutes)
- `avg_estimated_duration` - Average estimated duration (minutes)

## Performance Considerations

### Query Optimization

1. **Index Selection**: PostgreSQL automatically chooses optimal indexes
2. **Partial Indexes**: Reduce index size for filtered queries
3. **Covering Indexes**: Avoid table lookups for common queries
4. **BRIN Indexes**: Minimal overhead for time-series data

### Maintenance

#### Vacuum and Analyze
```sql
VACUUM ANALYZE tasks;
```

#### Reindex
```sql
REINDEX TABLE tasks;
```

#### Statistics Update
```sql
ANALYZE tasks;
```

## Sample Queries

### Find Active Tasks by Priority
```sql
SELECT id, title, priority, estimated_start
FROM tasks
WHERE status IN ('pending', 'in_progress')
ORDER BY priority DESC, estimated_start ASC;
```
**Uses**: `idx_tasks_active_by_priority`

### Search by Tags
```sql
SELECT id, title, tags
FROM tasks
WHERE tags @> ARRAY['frontend', 'ui'];
```
**Uses**: `idx_tasks_tags`

### Fuzzy Title Search
```sql
SELECT * FROM search_tasks('implemnt feture');
```
**Uses**: `idx_tasks_title_trgm` (handles typos)

### Metadata Query
```sql
SELECT id, title, metadata
FROM tasks
WHERE metadata @> '{"client": "Acme Corp"}';
```
**Uses**: `idx_tasks_metadata`

### Time Range Query
```sql
SELECT id, title, created_at
FROM tasks
WHERE created_at BETWEEN '2024-01-01' AND '2024-12-31';
```
**Uses**: `idx_tasks_created_at_brin`

## Backup and Recovery

### Backup
```bash
pg_dump -U taskuser -d taskdb -F c -b -v -f taskdb_backup.dump
```

### Restore
```bash
pg_restore -U taskuser -d taskdb -v taskdb_backup.dump
```

### Point-in-Time Recovery (PITR)
Enable in `postgresql.conf`:
```
wal_level = replica
archive_mode = on
archive_command = 'cp %p /path/to/archive/%f'
```

## Migration Strategy

### Adding New Columns
```sql
ALTER TABLE tasks ADD COLUMN new_column TYPE DEFAULT value;
```

### Modifying Domains
```sql
ALTER DOMAIN task_title DROP CONSTRAINT task_title_check;
ALTER DOMAIN task_title ADD CONSTRAINT new_check CHECK (...);
```

### Creating Indexes Online
```sql
CREATE INDEX CONCURRENTLY idx_new_index ON tasks(column);
```

## Related Documentation

- [Database Setup Guide](../../db/README.md) - Installation and configuration
- [System Architecture](system-architecture.md) - Overall system design
- [API Design](api-design.md) - How the API interacts with the database
