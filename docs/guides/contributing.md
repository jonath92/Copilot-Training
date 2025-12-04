# Contributing to Task Management Application

Thank you for your interest in contributing! This guide will help you get started.

## Project Context

This is a training repository designed to explore GitHub Copilot and AI-assisted development. The focus is on learning AI tools, not building a production-ready application.

## Getting Started

1. **Fork the Repository**: Create your own fork of the repository
2. **Clone Your Fork**: 
   ```bash
   git clone https://github.com/YOUR_USERNAME/Copilot-Training.git
   cd Copilot-Training
   ```
3. **Set Up Development Environment**: Follow the [Development Setup Guide](development-setup.md)

## Contribution Workflow

### 1. Create a Branch

Use descriptive branch names with prefixes:

```bash
# Features
git checkout -b feature/add-task-filtering

# Bug fixes
git checkout -b fix/task-validation-error

# Documentation
git checkout -b docs/update-api-docs

# Refactoring
git checkout -b refactor/database-queries
```

### 2. Make Your Changes

- Follow the coding standards (see below)
- Write clear, descriptive commit messages
- Test your changes locally
- Update documentation if needed

### 3. Commit Your Changes

Use conventional commits format:

```bash
git commit -m "feat: add task filtering by status"
git commit -m "fix: correct task duration validation"
git commit -m "docs: update API documentation"
git commit -m "test: add E2E tests for task creation"
```

**Commit Message Format**:
```
<type>: <description>

[optional body]

[optional footer]
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### 4. Push to Your Fork

```bash
git push origin feature/add-task-filtering
```

### 5. Create a Pull Request

1. Go to the original repository
2. Click "New Pull Request"
3. Select your fork and branch
4. Fill in the PR template (see below)
5. Submit the pull request

## Pull Request Guidelines

### PR Title

Use the same conventional commit format:
```
feat: add task filtering by status and priority
fix: resolve task validation error on empty description
docs: add comprehensive API documentation
```

### PR Description Template

```markdown
## Description
Brief description of what this PR does.

## Type of Change
- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update
- [ ] Refactoring (no functional changes)

## Changes Made
- Bullet point list of changes
- Another change
- And another

## Testing
Describe how you tested your changes:
- [ ] Manual testing performed
- [ ] Unit tests added/updated
- [ ] E2E tests added/updated
- [ ] All tests passing

## Screenshots (if applicable)
Add screenshots for UI changes.

## Checklist
- [ ] My code follows the project's coding standards
- [ ] I have performed a self-review of my code
- [ ] I have commented my code where necessary
- [ ] I have updated the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing tests pass locally
```

## Coding Standards

### Go (Backend)

Follow the guidelines in [`.github/instructions/go-copilot.instructions.md`](../../.github/instructions/go-copilot.instructions.md):

- Use `gofmt` and `goimports` for formatting
- Follow Go naming conventions (camelCase, MixedCaps)
- Document all exported types and functions
- Handle errors properly (don't ignore them)
- Use meaningful variable and function names

**Example**:
```go
// TaskHandler handles HTTP requests for task operations
type TaskHandler struct {
    db *sql.DB
}

// GetTask retrieves a task by ID
func (h *TaskHandler) GetTask(w http.ResponseWriter, r *http.Request) {
    id := r.URL.Query().Get("id")
    if id == "" {
        http.Error(w, "Missing task ID", http.StatusBadRequest)
        return
    }
    
    // Implementation...
}
```

### TypeScript (Frontend)

- Use TypeScript strict mode
- Define interfaces for all data structures
- Use async/await for asynchronous operations
- Follow consistent naming (camelCase for variables, PascalCase for types)
- Add JSDoc comments for complex functions

**Example**:
```typescript
interface Task {
  id: string;
  title: string;
  description?: string;
  status: TaskStatus;
}

type TaskStatus = 'pending' | 'in_progress' | 'completed';

/**
 * Fetches tasks from the API with pagination
 * @param limit - Maximum number of tasks to retrieve
 * @param offset - Number of tasks to skip
 * @returns Promise resolving to array of tasks
 */
async function getTasks(limit: number, offset: number): Promise<Task[]> {
  // Implementation...
}
```

### SQL (Database)

- Use meaningful table and column names
- Add comments for complex queries
- Use proper indentation
- Avoid SELECT * in production code
- Use parameterized queries (prevent SQL injection)

**Example**:
```sql
-- Retrieve active tasks sorted by priority
SELECT 
    id,
    title,
    priority,
    created_at
FROM tasks
WHERE status IN ('pending', 'in_progress')
ORDER BY 
    CASE priority
        WHEN 'urgent' THEN 1
        WHEN 'high' THEN 2
        WHEN 'medium' THEN 3
        WHEN 'low' THEN 4
    END,
    created_at DESC;
```

## Documentation Style

### General Guidelines

- Use clear, concise language
- Write in active voice
- Use present tense
- Avoid jargon unless necessary
- Define acronyms on first use

### Markdown Formatting

```markdown
# H1 for page titles
## H2 for major sections
### H3 for subsections

Use `code` for inline code and commands.

Use code blocks with language specification:
\`\`\`typescript
const example = "code";
\`\`\`

Use **bold** for emphasis, *italic* for terms.

Use [links](url) for references.

Use tables for structured data:
| Column | Description |
|--------|-------------|
| Data   | Details     |
```

### Documentation Structure

Follow the [Diátaxis framework](https://diataxis.fr/):

1. **Tutorials**: Step-by-step learning guides
2. **How-to Guides**: Problem-solving recipes
3. **Reference**: Technical descriptions
4. **Explanation**: Conceptual clarification

## Testing Requirements

### Backend Tests (Go)

```go
func TestGetTask(t *testing.T) {
    // Arrange
    handler := &TaskHandler{db: mockDB}
    req := httptest.NewRequest("GET", "/tasks?id=123", nil)
    w := httptest.NewRecorder()
    
    // Act
    handler.GetTask(w, req)
    
    // Assert
    if w.Code != http.StatusOK {
        t.Errorf("Expected status 200, got %d", w.Code)
    }
}
```

### E2E Tests (Playwright)

```typescript
import { test, expect } from '@playwright/test';

test('should create a new task', async ({ page }) => {
  await page.goto('http://localhost:5173');
  
  await page.fill('[data-testid="task-title"]', 'New Task');
  await page.fill('[data-testid="task-duration"]', '60');
  await page.click('[data-testid="create-task"]');
  
  await expect(page.locator('[data-testid="task-list"]'))
    .toContainText('New Task');
});
```

## CI/CD Pipeline

All pull requests trigger automated checks:

1. **Path Filtering**: Determines which components changed
2. **Database Build**: Builds and tests database changes
3. **Backend Tests**: Runs Go tests
4. **Frontend Build**: Compiles TypeScript and builds frontend
5. **E2E Tests**: Runs Playwright tests

Ensure all checks pass before requesting review.

## Code Review Process

### What Reviewers Look For

- Code follows project standards
- Changes are well-tested
- Documentation is updated
- No unnecessary complexity
- Security considerations addressed
- Performance implications considered

### Responding to Feedback

- Be respectful and professional
- Ask questions if feedback is unclear
- Make requested changes in new commits
- Mark conversations as resolved when addressed

## Community Guidelines

### Code of Conduct

- Be respectful and inclusive
- Welcome newcomers
- Accept constructive criticism gracefully
- Focus on what's best for the project
- Show empathy towards others

### Getting Help

- Check [existing documentation](../README.md)
- Search [closed issues](https://github.com/jonath92/Copilot-Training/issues?q=is%3Aissue+is%3Aclosed)
- Ask in [discussions](https://github.com/jonath92/Copilot-Training/discussions)
- Open a new issue for bugs

## Issue Reporting

### Bug Reports

```markdown
**Describe the bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What you expected to happen.

**Screenshots**
If applicable, add screenshots.

**Environment:**
- OS: [e.g., macOS 14]
- Go version: [e.g., 1.21]
- Node version: [e.g., 18.17]
- Database: [e.g., PostgreSQL 15]
```

### Feature Requests

```markdown
**Is your feature request related to a problem?**
A clear description of the problem.

**Describe the solution you'd like**
What you want to happen.

**Describe alternatives you've considered**
Other solutions you've thought about.

**Additional context**
Any other context or screenshots.
```

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (see LICENSE file).

## Questions?

If you have questions about contributing, please:
- Check the [documentation](../README.md)
- Open a [discussion](https://github.com/jonath92/Copilot-Training/discussions)
- Reach out to maintainers

## Thank You!

Every contribution, no matter how small, helps improve the project. Thank you for taking the time to contribute!
