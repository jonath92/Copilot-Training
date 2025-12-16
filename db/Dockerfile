FROM postgres:16-alpine

# PostgreSQL credentials must be provided via environment variables at runtime.
# Do not hard-code POSTGRES_USER, POSTGRES_PASSWORD, or POSTGRES_DB here.


# Copy initialization script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose the default PostgreSQL port
EXPOSE 5432
