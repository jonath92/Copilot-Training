# Database Configuration

This directory contains the PostgreSQL database configuration and initialization files for the task management application.

## Overview

The database uses PostgreSQL with advanced features including:
- Custom domains for data validation
- ENUM types for status and priority
- JSONB for flexible metadata storage
- Array fields for tags
- Trigram matching for fuzzy search
- Multiple index types (GIN, GiST, BRIN) for optimal query performance

## Files

- **docker-compose.yml**: Docker Compose configuration for running PostgreSQL
- **Dockerfile**: Docker image configuration
- **init.sql**: Database initialization script with schema, indexes, and sample data

## Database Schema

### Tasks Table

The main `tasks` table includes:

| Column | Type | Description |
|--------|------|-------------|
| id | UUID | Primary key, auto-generated |
| title | task_title | Task title (3-255 characters, validated) |
| description | TEXT | Optional task description |
| duration | positive_duration | Duration in minutes (1-10080) |
| status | task_status | Enum: pending, in_progress, completed, cancelled, archived |
| priority | task_priority | Enum: low, medium, high, urgent |
| tags | TEXT[] | Array of tags for categorization |
| metadata | JSONB | Flexible metadata storage |
| estimated_start | TIMESTAMPTZ | Estimated start time |
| estimated_end | TIMESTAMPTZ | Estimated end time |
| actual_start | TIMESTAMPTZ | Actual start time |
| actual_end | TIMESTAMPTZ | Actual end time |
| created_at | TIMESTAMPTZ | Record creation timestamp |
| updated_at | TIMESTAMPTZ | Last update timestamp (auto-updated) |
| created_by | TEXT | User who created the task |

### Custom Types

- **task_title**: Domain with validation for title length and content
- **positive_duration**: Domain ensuring duration is between 1 and 10,080 minutes (1 week)
- **task_status**: ENUM for task lifecycle states
- **task_priority**: ENUM for priority levels

### Views

- **task_analytics**: Provides analytics with running statistics, time analysis, and completion rates

### Functions

- **update_updated_at_column()**: Trigger function to auto-update the `updated_at` timestamp
- **search_tasks(search_term)**: Fuzzy search function using trigram similarity

## Quick Start

### Using Docker Compose

1. Set the PostgreSQL password environment variable:
```bash
export POSTGRES_PASSWORD=your_secure_password
```

2. Start the database:
```bash
docker-compose up -d
```

3. Connect to the database:
```bash
psql -h localhost -U taskuser -d taskdb
```

### Manual Setup

If running PostgreSQL directly:

```bash
psql -U postgres -f init.sql
```

## Database Connection

- **Host**: localhost
- **Port**: 5432
- **Database**: taskdb
- **User**: taskuser
- **Password**: Set via `POSTGRES_PASSWORD` environment variable

## Features

### Advanced Indexing

The database uses multiple index types for optimal performance:
- Composite indexes for common query patterns
- Partial indexes for active tasks
- GIN indexes for array and JSONB operations
- GiST indexes for trigram similarity searches
- BRIN indexes for time-based queries
- Covering indexes to avoid table lookups

### Data Validation

- Custom domains ensure data integrity at the database level
- CHECK constraints validate time ranges and duration matches
- ENUM types restrict status and priority values

### Extensions

The following PostgreSQL extensions are enabled:
- **uuid-ossp**: UUID generation
- **pgcrypto**: Cryptographic functions
- **pg_trgm**: Trigram matching for fuzzy search
- **btree_gin**: GIN indexes for btree types

## Sample Data

The initialization script includes 5 sample tasks demonstrating various features including tags, metadata, and different statuses.

## Maintenance

### Backup

```bash
pg_dump -U taskuser -d taskdb > backup.sql
```

### Restore

```bash
psql -U taskuser -d taskdb < backup.sql
```

### View Database Stats

```sql
SELECT * FROM task_analytics;
```

## Development

When making schema changes:
1. Update `init.sql` with the new schema
2. Test the changes locally
3. Create migration scripts for existing deployments
4. Update this README with any schema changes
