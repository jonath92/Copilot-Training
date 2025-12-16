# Project Overview

This project is a task estimation web application that allows users to create, read, update, and delete tasks. It follows a three-tier architecture with a React frontend, Node.js backend, and MongoDB database.

## Folder Structure

- `/frontend`: Contains the React frontend source code.
- `/backend`: Contains the Node.js backend API source code.
- `/db`: Contains database configuration and scripts for MongoDB.
- `/api-spec`: Contains API specifications and documentation.
- `/e2e`: Contains end-to-end tests for the application.
- `/scripts`: Contains utility scripts for development and deployment.
- `/tutorial`: Contains tutorial documentation for building this application.
- `/.github/workflows`: Contains CI/CD pipeline configuration.

## Libraries and Frameworks

- React and Tailwind CSS for the frontend.
- Node.js and Express for the backend.
- MongoDB for data storage.
- Docker for containerization.

## Data Model

Tasks have the following properties:
- ID (unique identifier)
- Title (string)
- Description (string)
- Estimated Time to Complete (number or duration)

## Coding Standards

- Use semicolons at the end of each statement.
- Use single quotes for strings.
- Use arrow functions for callbacks.
- Follow RESTful API design principles for the backend.
