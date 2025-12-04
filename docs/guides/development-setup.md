# Development Setup

Complete guide for setting up your development environment for the Task Management Application.

## System Requirements

### Minimum Requirements
- **OS**: Windows 10+, macOS 11+, or Linux (Ubuntu 20.04+)
- **RAM**: 8 GB
- **Disk Space**: 5 GB free
- **Internet**: Broadband connection for downloading dependencies

### Recommended Requirements
- **RAM**: 16 GB or more
- **Disk Space**: 20 GB free (for Docker images and dependencies)
- **Display**: 1920x1080 or higher

## Required Software

### 1. Git
Version control system for managing code.

**Installation**:
- **macOS**: `brew install git`
- **Linux**: `sudo apt-get install git`
- **Windows**: Download from [git-scm.com](https://git-scm.com/)

**Verify**:
```bash
git --version
# Should output: git version 2.x.x
```

### 2. Docker & Docker Compose
Container platform for running PostgreSQL.

**Installation**:
- **macOS**: [Docker Desktop for Mac](https://docs.docker.com/desktop/install/mac-install/)
- **Linux**: [Docker Engine](https://docs.docker.com/engine/install/)
- **Windows**: [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)

**Verify**:
```bash
docker --version
# Should output: Docker version 24.x.x

docker-compose --version
# Should output: Docker Compose version v2.x.x
```

### 3. Go
Backend programming language.

**Installation**:
- **macOS**: `brew install go`
- **Linux**: Download from [go.dev/dl](https://go.dev/dl/)
- **Windows**: Download installer from [go.dev/dl](https://go.dev/dl/)

**Verify**:
```bash
go version
# Should output: go version go1.21.x or higher
```

**Configure GOPATH** (optional):
```bash
# Add to ~/.bashrc or ~/.zshrc
export GOPATH=$HOME/go
export PATH=$PATH:$GOPATH/bin
```

### 4. Node.js & npm
Frontend development environment.

**Installation**:
- **macOS**: `brew install node`
- **Linux**: 
  ```bash
  curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
  sudo apt-get install -y nodejs
  ```
- **Windows**: Download from [nodejs.org](https://nodejs.org/)

**Verify**:
```bash
node --version
# Should output: v18.x.x or higher

npm --version
# Should output: 9.x.x or higher
```

### 5. PostgreSQL Client (Optional)
Command-line tool for database access.

**Installation**:
- **macOS**: `brew install postgresql`
- **Linux**: `sudo apt-get install postgresql-client`
- **Windows**: Download from [postgresql.org](https://www.postgresql.org/download/windows/)

**Verify**:
```bash
psql --version
# Should output: psql (PostgreSQL) 15.x
```

## IDE Setup

### Visual Studio Code (Recommended)

**Installation**:
Download from [code.visualstudio.com](https://code.visualstudio.com/)

**Recommended Extensions**:

1. **Go** (`golang.go`)
   - Go language support
   - IntelliSense, debugging, testing

2. **GitHub Copilot** (`GitHub.copilot`)
   - AI-powered code suggestions
   - Core feature for this training

3. **GitHub Copilot Chat** (`GitHub.copilot-chat`)
   - AI chat interface in VS Code
   - Ask questions about code

4. **Prettier** (`esbenp.prettier-vscode`)
   - Code formatter for TypeScript/JavaScript

5. **ESLint** (`dbaeumer.vscode-eslint`)
   - Linting for TypeScript/JavaScript

6. **PostgreSQL** (`ckolkman.vscode-postgres`)
   - Database management in VS Code

7. **Docker** (`ms-azuretools.vscode-docker`)
   - Docker container management

8. **REST Client** (`humao.rest-client`)
   - Test API endpoints directly in VS Code

**Install Extensions**:
```bash
code --install-extension golang.go
code --install-extension GitHub.copilot
code --install-extension GitHub.copilot-chat
code --install-extension esbenp.prettier-vscode
code --install-extension dbaeumer.vscode-eslint
code --install-extension ckolkman.vscode-postgres
code --install-extension ms-azuretools.vscode-docker
code --install-extension humao.rest-client
```

**VS Code Settings**:

Create `.vscode/settings.json`:
```json
{
  "go.useLanguageServer": true,
  "go.lintTool": "golangci-lint",
  "go.formatTool": "goimports",
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true
  },
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[go]": {
    "editor.defaultFormatter": "golang.go"
  }
}
```

### Alternative IDEs

**GoLand** (JetBrains):
- Professional Go IDE
- Built-in database tools
- Advanced refactoring

**WebStorm** (JetBrains):
- Professional JavaScript/TypeScript IDE
- Excellent for frontend development

**Cursor**:
- AI-first code editor
- Built-in AI chat and editing

## Project Setup

### 1. Clone Repository

```bash
git clone https://github.com/jonath92/Copilot-Training.git
cd Copilot-Training
```

### 2. Database Setup

```bash
cd db

# Set PostgreSQL password
export POSTGRES_PASSWORD=your_secure_password

# Start PostgreSQL
docker-compose up -d

# Verify database is running
docker-compose ps

# Check logs
docker-compose logs -f
```

**Connect to database**:
```bash
psql -h localhost -U taskuser -d taskdb
# Password: your_secure_password

# List tables
\dt

# Exit
\q
```

### 3. Backend Setup

```bash
cd ../backend

# Download Go dependencies
go mod download

# Run tests
go test ./...

# Start server
go run main.go
```

**Verify backend**:
```bash
curl http://localhost:8080/health
# Expected: {"status":"healthy",...}
```

### 4. Frontend Setup

```bash
cd ../frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

**Verify frontend**:
Open browser to `http://localhost:5173`

### 5. E2E Tests Setup

```bash
cd ../e2e

# Install dependencies
npm install

# Install Playwright browsers
npx playwright install

# Run tests (requires backend and frontend running)
npm test
```

## Development Tools

### Go Tools

**Install development tools**:
```bash
# Linter
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest

# Import formatter
go install golang.org/x/tools/cmd/goimports@latest

# Live reload
go install github.com/cosmtrek/air@latest

# API code generator (optional)
go install github.com/deepmap/oapi-codegen/cmd/oapi-codegen@latest
```

**Run linter**:
```bash
cd backend
golangci-lint run
```

**Live reload during development**:
```bash
cd backend
air
# Server will restart on code changes
```

### Frontend Tools

**Optional global packages**:
```bash
# TypeScript compiler
npm install -g typescript

# Vite CLI
npm install -g vite
```

## Environment Variables

Create `.env` files for environment-specific configuration:

**backend/.env**:
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_USER=taskuser
DB_PASSWORD=your_secure_password
DB_NAME=taskdb

# Server
PORT=8080
LOG_LEVEL=debug

# Environment
ENVIRONMENT=development
```

**frontend/.env.development**:
```bash
VITE_API_URL=http://localhost:8080/api/v1
```

**frontend/.env.production**:
```bash
VITE_API_URL=https://api.production.com/api/v1
```

## Database Management

### GUI Tools

**pgAdmin**:
```bash
docker run -p 5050:80 \
  -e PGADMIN_DEFAULT_EMAIL=admin@admin.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin \
  -d dpage/pgadmin4
```
Open: `http://localhost:5050`

**DBeaver** (Desktop):
- Download from [dbeaver.io](https://dbeaver.io/)
- Cross-platform database tool
- Supports PostgreSQL

### Command Line

**Backup database**:
```bash
pg_dump -h localhost -U taskuser -d taskdb > backup.sql
```

**Restore database**:
```bash
psql -h localhost -U taskuser -d taskdb < backup.sql
```

**Reset database**:
```bash
cd db
docker-compose down -v
docker-compose up -d
```

## Testing Tools

### API Testing

**cURL**:
```bash
# GET request
curl http://localhost:8080/api/v1/tasks

# POST request
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","duration":30,"priority":"medium"}'
```

**HTTPie** (alternative to cURL):
```bash
# Install
brew install httpie  # macOS
pip install httpie   # Python

# Usage
http GET localhost:8080/api/v1/tasks
http POST localhost:8080/api/v1/tasks title="Test" duration:=30 priority=medium
```

**Postman**:
- Download from [postman.com](https://www.postman.com/)
- Import OpenAPI spec: `api-spec/api.yaml`

### E2E Testing

**Playwright UI Mode**:
```bash
cd e2e
npm run test:ui
```

**Headed mode (see browser)**:
```bash
npm run test:headed
```

**Debug mode**:
```bash
npx playwright test --debug
```

## Troubleshooting

### Docker Issues

**Port already in use**:
```bash
# Find process using port 5432
lsof -i :5432
# Kill process
kill -9 <PID>
```

**Permission denied**:
```bash
# Linux: Add user to docker group
sudo usermod -aG docker $USER
# Log out and back in
```

### Go Issues

**Module errors**:
```bash
cd backend
go mod tidy
go mod download
```

**GOPATH issues**:
```bash
go env GOPATH
# Should output: /Users/you/go (or similar)
```

### Node Issues

**Package installation fails**:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

**Port already in use**:
```bash
# Kill process on port 5173
lsof -i :5173
kill -9 <PID>
```

### Database Connection Issues

**Cannot connect**:
1. Verify Docker is running: `docker ps`
2. Check container logs: `docker-compose logs`
3. Verify password: `echo $POSTGRES_PASSWORD`
4. Test connection: `psql -h localhost -U taskuser -d taskdb`

## Next Steps

- Read the [Architecture Documentation](../architecture/system-architecture.md)
- Follow the [Tutorial Materials](../../tutorial/)
- Review [Contributing Guidelines](contributing.md)
- Explore the [API Documentation](../api/README.md)

## Getting Help

- Check [Troubleshooting Guide](troubleshooting.md)
- Search [GitHub Issues](https://github.com/jonath92/Copilot-Training/issues)
- Ask in [GitHub Discussions](https://github.com/jonath92/Copilot-Training/discussions)
- Open a new issue for bugs or questions
