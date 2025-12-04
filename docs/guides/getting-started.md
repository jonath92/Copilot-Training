# Getting Started

This guide helps you quickly set up and run the Task Management Application on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Git** - Version control
- **Docker & Docker Compose** - For running the database
- **Go** 1.21 or higher - Backend runtime
- **Node.js** v16 or higher - Frontend development
- **PostgreSQL client** (optional) - For database exploration

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/jonath92/Copilot-Training.git
cd Copilot-Training
```

### 2. Start the Database

```bash
cd db
export POSTGRES_PASSWORD=your_secure_password
docker-compose up -d
cd ..
```

The database will be available at `localhost:5432` with:
- Database: `taskdb`
- User: `taskuser`
- Password: Value from `POSTGRES_PASSWORD`

### 3. Start the Backend

```bash
cd backend
go run main.go
```

The backend will start on `http://localhost:8080`. Test the health endpoint:

```bash
curl http://localhost:8080/health
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2024-12-04T10:30:00Z",
  "service": "task-management-backend"
}
```

### 4. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:5173` (or the port Vite assigns).

## Verify Your Setup

1. **Database**: Connect with `psql -h localhost -U taskuser -d taskdb` and run `\dt` to see tables
2. **Backend**: Visit `http://localhost:8080/health` - should return healthy status
3. **Frontend**: Open `http://localhost:5173` in your browser

## Next Steps

- [Development Setup](development-setup.md) - Detailed configuration and tools
- [Architecture Overview](../architecture/system-architecture.md) - Understand the system design
- [API Documentation](../api/README.md) - Explore available endpoints
- [Testing Guide](testing.md) - Run and write tests

## Common Issues

### Database Won't Start

- Ensure port 5432 is not in use: `lsof -i :5432`
- Check Docker is running: `docker ps`
- View logs: `docker-compose logs -f`

### Backend Connection Errors

- Verify the database is running
- Check `POSTGRES_PASSWORD` environment variable is set
- Ensure backend configuration matches database credentials

### Frontend Build Errors

- Delete `node_modules` and run `npm install` again
- Check Node.js version: `node --version` (should be 16+)
- Clear Vite cache: `rm -rf node_modules/.vite`

## Training Mode

This repository is designed as a training resource for exploring GitHub Copilot and AI-assisted development:

- Focus on learning AI development tools, not perfection
- Some features may be incomplete - that's intentional
- Follow the [tutorial materials](../../tutorial/) for guided learning
- Experiment with different AI coding techniques

For detailed training instructions, see the [Tutorial README](../../tutorial/).
