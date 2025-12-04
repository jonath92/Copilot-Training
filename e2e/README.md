# End-to-End Testing

This directory contains end-to-end (E2E) tests for the task management web application using [Playwright](https://playwright.dev/).

## Overview

The E2E tests validate the complete user workflows by testing the interaction between the frontend, backend, and database components in a realistic environment.

## Setup

### Prerequisites

- Node.js (v16 or higher)
- The application backend and frontend should be running

### Installation

1. Install dependencies:
   ```bash
   npm install
   ```

2. Install Playwright browsers:
   ```bash
   npx playwright install
   ```

## Running Tests

### Command Line

- **Run all tests**: `npm test`
- **Run tests with browser UI**: `npm run test:headed`
- **Interactive test runner**: `npm run test:ui`
- **Show test report**: `npm run report`

### Available Scripts

| Script | Description |
|--------|-------------|
| `test` | Run all tests in headless mode |
| `test:headed` | Run tests with browser UI visible |
| `test:ui` | Launch Playwright's interactive test runner |
| `report` | Display the HTML test report |

## Test Structure

```
e2e/
├── tests/           # Test files
├── fixtures/        # Test data and helpers
├── page-objects/    # Page object models
└── playwright.config.js  # Playwright configuration
```

## Writing Tests

Tests should be organized by feature or user workflow:

- **Task Management**: Creating, editing, deleting tasks
- **Task Estimation**: Duration estimation functionality
- **User Interface**: Navigation and UI interactions

### Best Practices

1. Use page object models for reusable interactions
2. Include both positive and negative test scenarios
3. Test across different browsers (Chromium, Firefox, WebKit)
4. Use meaningful test descriptions and organize with `describe` blocks
5. Clean up test data after each test run

## Configuration

The Playwright configuration can be found in `playwright.config.js`. Key settings include:

- Browser configuration (Chromium, Firefox, WebKit)
- Base URL for the application
- Test timeout settings
- Report generation options

## Continuous Integration

These tests are designed to run in CI/CD pipelines to ensure application quality before deployments.

## Troubleshooting

### Common Issues

1. **Tests timing out**: Increase timeout values in configuration
2. **Element not found**: Check selectors and wait conditions
3. **Application not responding**: Ensure backend and frontend are running

### Debug Mode

Run tests in debug mode for troubleshooting:
```bash
npx playwright test --debug
```

For more information, see the [Playwright documentation](https://playwright.dev/docs/intro).