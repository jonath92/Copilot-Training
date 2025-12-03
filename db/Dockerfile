FROM postgres:16-alpine

# Set environment variables
ENV POSTGRES_DB=tasks_db
ENV POSTGRES_USER=tasks_user
ENV POSTGRES_PASSWORD=tasks_password

# Copy initialization script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432
