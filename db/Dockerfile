# PostgreSQL Database for Task Estimation Application
FROM postgres:16-alpine

# Set default environment variables for PostgreSQL
# Note: POSTGRES_PASSWORD should be provided at runtime, not hardcoded
ENV POSTGRES_DB=taskdb
ENV POSTGRES_USER=taskuser

# Copy initialization script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432

# Health check to verify database is ready
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB} || exit 1
