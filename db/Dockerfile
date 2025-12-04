# Use the official PostgreSQL image
FROM postgres:15

# Set environment variables
ENV POSTGRES_DB=taskdb
ENV POSTGRES_USER=taskuser
ENV POSTGRES_PASSWORD=taskpass

# Copy initialization script
COPY init.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432


