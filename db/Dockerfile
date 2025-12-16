FROM postgres:16-alpine

# Set environment variables for PostgreSQL
ENV POSTGRES_USER=taskuser
ENV POSTGRES_PASSWORD=taskpassword
ENV POSTGRES_DB=taskdb

# Copy initialization script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose the default PostgreSQL port
EXPOSE 5432
