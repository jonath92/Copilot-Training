# Introduction

The way we have developed software in the past and how we will develop software in the future has changed. AI is now a standard tool in the developer's toolbox but due to the rapid pace of change, it is hard to keep up with the newest techniques and tools AI has to offer. We want to use the training to explore new opportunites of AI in the full software development lifecycle. 

For this purpose, we will build a sample application where we will explore different AI tools that can help us in different stages of the software development lifecycle.

# Sample Application

>[!NOTE]
> The sample application is used to explore different GitHub Copilot tools and techniques. Don't focus too much on the implementation details of the application itself and don't worry if some parts are not fully functional. 

As Software Developers, we are often asked to give estimations on how long it will take to implement a new feature or even a complete new application. Due to many unkown factors and changing requirements, this is very hard to do accurately. While it is understandable that stakeholders are interested in an estimation, it also creates pressue to the developers to deliver within the given timeframe. This pressure can be reduced by breaking down the implementation into smaller tasks and making the estimation more transparent by documenting the assumptions that were made during the estimation process. That way it is easier for developers to justify a delay in case the requirements change or unexpected technical challenges arise.

To explore how AI can help us in this task, we will build a sample application which should fullfill the following requirements:

- A three-tier architecture with a frontend, backend and database
- CI/CD pipeline to automatically build, test and deploy the application (for simplicity, we only build the docker images in the pipeline but do not deploy them)
- The application should allow users to create, read, update and delete Tasks. Tasks have the following properties: 
    - ID (unique identifier)
    - Title (string)
    - Description (string)
    - Estimated Time to Complete 
- End2End tests to verify the functionality of the application

# Documentation

Comprehensive documentation is available in the [`docs/`](docs/) directory:

## Quick Links

- **[Getting Started Guide](docs/guides/getting-started.md)** - Setup and run the application
- **[Development Setup](docs/guides/development-setup.md)** - Detailed environment configuration
- **[API Documentation](docs/api/README.md)** - Complete API reference with examples
- **[Contributing Guidelines](docs/guides/contributing.md)** - How to contribute to the project

## Architecture

- **[System Architecture](docs/architecture/system-architecture.md)** - Overall design and component interactions
- **[Database Design](docs/architecture/database-design.md)** - PostgreSQL schema and optimization
- **[API Design](docs/architecture/api-design.md)** - RESTful API conventions and endpoints

## Component Documentation

- **[Backend](backend/README.md)** - Go backend service documentation
- **[Frontend](frontend/README.md)** - Vite + TypeScript frontend documentation
- **[Database](db/README.md)** - PostgreSQL setup and schema
- **[E2E Testing](e2e/README.md)** - End-to-end testing with Playwright

## Guides

- **[Testing Guide](docs/guides/testing.md)** - Testing strategies and practices
- **[Tutorial Materials](tutorial/)** - Step-by-step training modules

# Training Structure

The [tutorial](tutorial/) directory contains step-by-step modules for exploring GitHub Copilot and AI-assisted development. Follow the tutorials in order to learn different AI coding techniques and tools.

# TODO: 

- https://github.com/spark
- 


# References
- [VS Code Copilot Cheat Sheet](https://code.visualstudio.com/docs/copilot/reference/copilot-vscode-features)
- [Awesome GitHub Copilot Customizations](https://github.com/github/awesome-copilot)
- [GitHub Copilot Blog](https://github.blog)
- [GitHub Agentic Workflows](https://githubnext.github.io/gh-aw/)