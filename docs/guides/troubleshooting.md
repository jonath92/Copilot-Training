# Troubleshooting Guide

Common issues and solutions for the Task Management Application.

## Database Issues

### PostgreSQL Won't Start

**Symptoms**: Docker container fails to start or exits immediately

**Solutions**:

1. **Port 5432 already in use**:
   ```bash
   # Find what's using the port
   lsof -i :5432
   # or on Linux
   sudo netstat -tulpn | grep 5432
   
   # Stop the conflicting service
   sudo systemctl stop postgresql  # If system PostgreSQL is running
   # or kill the process
   kill -9 <PID>
   ```

2. **Permission errors**:
   ```bash
   # Fix Docker permissions (Linux)
   sudo usermod -aG docker $USER
   # Log out and back in
   
   # Reset database directory permissions
   cd db
   docker-compose down -v
   docker-compose up -d
   ```

3. **Check logs**:
   ```bash
   cd db
   docker-compose logs -f
   ```

### Cannot Connect to Database

**Symptoms**: `connection refused` or `authentication failed`

**Solutions**:

1. **Verify database is running**:
   ```bash
   docker-compose ps
   # Should show postgres container as "Up"
   ```

2. **Check environment variable**:
   ```bash
   echo $POSTGRES_PASSWORD
   # If empty, set it:
   export POSTGRES_PASSWORD=your_password
   ```

3. **Test connection**:
   ```bash
   psql -h localhost -U taskuser -d taskdb
   # Enter password when prompted
   ```

4. **Verify Docker network**:
   ```bash
   docker network ls
   docker network inspect db_default
   ```

### Database Schema Issues

**Symptoms**: Missing tables or columns

**Solutions**:

1. **Reset database**:
   ```bash
   cd db
   docker-compose down -v  # Removes volumes!
   docker-compose up -d
   ```

2. **Manually run init script**:
   ```bash
   psql -h localhost -U taskuser -d taskdb < init.sql
   ```

3. **Verify schema**:
   ```bash
   psql -h localhost -U taskuser -d taskdb
   \dt        # List tables
   \d tasks   # Describe tasks table
   ```

## Backend Issues

### Backend Won't Start

**Symptoms**: Go server exits or fails to start

**Solutions**:

1. **Port 8080 already in use**:
   ```bash
   # Find and kill process
   lsof -i :8080
   kill -9 <PID>
   ```

2. **Module errors**:
   ```bash
   cd backend
   go mod download
   go mod tidy
   ```

3. **Build errors**:
   ```bash
   # Clear Go cache
   go clean -cache -modcache
   
   # Reinstall dependencies
   go mod download
   
   # Try building
   go build -v
   ```

4. **Check Go version**:
   ```bash
   go version
   # Should be 1.21 or higher
   ```

### Database Connection Errors

**Symptoms**: Backend can't connect to PostgreSQL

**Solutions**:

1. **Verify environment variables**:
   ```bash
   # Create backend/.env
   DB_HOST=localhost
   DB_PORT=5432
   DB_USER=taskuser
   DB_PASSWORD=your_password
   DB_NAME=taskdb
   ```

2. **Test connection string**:
   ```bash
   psql "postgresql://taskuser:your_password@localhost:5432/taskdb"
   ```

3. **Check database is accepting connections**:
   ```bash
   docker-compose logs postgres
   # Look for "database system is ready to accept connections"
   ```

### API Errors

**Symptoms**: 500 errors or unexpected responses

**Solutions**:

1. **Check server logs**:
   ```bash
   # If running with go run
   # Logs appear in terminal
   
   # If running as service
   journalctl -u task-backend -f
   ```

2. **Test health endpoint**:
   ```bash
   curl http://localhost:8080/health
   # Should return: {"status":"healthy",...}
   ```

3. **Verify database connectivity**:
   ```bash
   # From inside backend container or process
   # Check if database is reachable
   ```

## Frontend Issues

### Frontend Won't Start

**Symptoms**: Vite fails to start or shows errors

**Solutions**:

1. **Port 5173 already in use**:
   ```bash
   # Use different port
   npm run dev -- --port 3000
   ```

2. **Module errors**:
   ```bash
   cd frontend
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **TypeScript errors**:
   ```bash
   # Check TypeScript compilation
   npx tsc --noEmit
   
   # Update TypeScript
   npm install -D typescript@latest
   ```

4. **Clear Vite cache**:
   ```bash
   rm -rf node_modules/.vite
   npm run dev
   ```

### Build Failures

**Symptoms**: `npm run build` fails

**Solutions**:

1. **Check TypeScript errors**:
   ```bash
   npx tsc --noEmit
   # Fix any type errors shown
   ```

2. **Increase Node memory**:
   ```bash
   export NODE_OPTIONS="--max-old-space-size=4096"
   npm run build
   ```

3. **Check dependencies**:
   ```bash
   npm audit fix
   npm update
   ```

### API Connection Issues

**Symptoms**: Frontend can't reach backend API

**Solutions**:

1. **Check backend is running**:
   ```bash
   curl http://localhost:8080/health
   ```

2. **Verify API URL**:
   ```bash
   # Check .env.development
   cat frontend/.env.development
   # Should contain: VITE_API_URL=http://localhost:8080/api/v1
   ```

3. **CORS errors**:
   ```typescript
   // Backend needs to allow frontend origin
   // Add CORS middleware in Go backend
   ```

## E2E Testing Issues

### Playwright Tests Fail

**Symptoms**: Tests timeout or fail unexpectedly

**Solutions**:

1. **Ensure services are running**:
   ```bash
   # Terminal 1: Database
   cd db && docker-compose up
   
   # Terminal 2: Backend
   cd backend && go run main.go
   
   # Terminal 3: Frontend
   cd frontend && npm run dev
   
   # Terminal 4: Tests
   cd e2e && npm test
   ```

2. **Install browsers**:
   ```bash
   cd e2e
   npx playwright install --with-deps
   ```

3. **Increase timeouts**:
   ```typescript
   // playwright.config.ts
   export default defineConfig({
     timeout: 60000,  // Increase from default
   })
   ```

4. **Run in headed mode**:
   ```bash
   npm run test:headed
   # See what's happening in the browser
   ```

5. **Debug specific test**:
   ```bash
   npx playwright test --debug tests/tasks.spec.ts
   ```

### Browser Launch Failures

**Symptoms**: Playwright can't launch browser

**Solutions**:

1. **Linux missing dependencies**:
   ```bash
   # Install system dependencies
   npx playwright install-deps
   ```

2. **Reinstall browsers**:
   ```bash
   npx playwright install --force
   ```

## Docker Issues

### Docker Won't Start

**Symptoms**: Docker daemon not running

**Solutions**:

1. **Start Docker Desktop** (macOS/Windows):
   - Open Docker Desktop application
   - Wait for it to fully start

2. **Start Docker service** (Linux):
   ```bash
   sudo systemctl start docker
   sudo systemctl enable docker  # Auto-start on boot
   ```

3. **Check Docker status**:
   ```bash
   docker info
   ```

### Container Issues

**Symptoms**: Containers exit unexpectedly

**Solutions**:

1. **View logs**:
   ```bash
   docker-compose logs -f [service-name]
   ```

2. **Restart containers**:
   ```bash
   docker-compose restart
   ```

3. **Rebuild containers**:
   ```bash
   docker-compose down
   docker-compose up --build
   ```

4. **Remove and recreate**:
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

## Performance Issues

### Slow Database Queries

**Solutions**:

1. **Check query plan**:
   ```sql
   EXPLAIN ANALYZE SELECT * FROM tasks WHERE status = 'pending';
   ```

2. **Update statistics**:
   ```sql
   ANALYZE tasks;
   ```

3. **Reindex**:
   ```sql
   REINDEX TABLE tasks;
   ```

### High Memory Usage

**Solutions**:

1. **Limit Docker resources**:
   ```bash
   # Edit docker-compose.yml
   services:
     postgres:
       deploy:
         resources:
           limits:
             memory: 1G
   ```

2. **Check for memory leaks**:
   ```bash
   # Go backend
   go run -race main.go
   
   # Monitor with pprof
   import _ "net/http/pprof"
   ```

## Development Tools Issues

### Git Issues

**Symptoms**: Git conflicts or errors

**Solutions**:

1. **Merge conflicts**:
   ```bash
   git status
   # Edit conflicted files
   git add .
   git commit
   ```

2. **Reset to remote**:
   ```bash
   git fetch origin
   git reset --hard origin/main
   ```

3. **Clean untracked files**:
   ```bash
   git clean -fd
   ```

### IDE Issues

**VS Code Slow**:

1. **Disable extensions**:
   - Disable unused extensions
   - Reload window

2. **Clear cache**:
   ```bash
   # macOS
   rm -rf ~/Library/Application\ Support/Code/Cache
   
   # Linux
   rm -rf ~/.config/Code/Cache
   ```

3. **Increase memory**:
   ```json
   // settings.json
   {
     "files.watcherExclude": {
       "**/node_modules/**": true
     }
   }
   ```

## Network Issues

### DNS Resolution Failures

**Solutions**:

1. **Use IP instead of localhost**:
   ```bash
   # Instead of localhost
   curl http://127.0.0.1:8080/health
   ```

2. **Check /etc/hosts** (Linux/macOS):
   ```bash
   # Should contain:
   127.0.0.1 localhost
   ```

### Firewall Blocking Connections

**Solutions**:

1. **Allow ports**:
   ```bash
   # macOS
   # System Preferences > Security & Privacy > Firewall
   
   # Linux (ufw)
   sudo ufw allow 8080
   sudo ufw allow 5432
   sudo ufw allow 5173
   ```

## Getting More Help

### Before Asking for Help

1. **Check logs**: Look for error messages
2. **Search issues**: Check existing GitHub issues
3. **Verify versions**: Ensure you're using supported versions
4. **Try clean install**: Remove and reinstall dependencies

### Where to Get Help

1. **Documentation**:
   - [Getting Started](getting-started.md)
   - [Development Setup](development-setup.md)
   - [Architecture](../architecture/system-architecture.md)

2. **GitHub**:
   - [Open an issue](https://github.com/jonath92/Copilot-Training/issues/new)
   - [Search discussions](https://github.com/jonath92/Copilot-Training/discussions)

3. **Community**:
   - GitHub Discussions
   - Training instructor (if in workshop)

### Creating a Bug Report

Include:

1. **Environment**:
   - OS and version
   - Go version (`go version`)
   - Node version (`node --version`)
   - Docker version (`docker --version`)

2. **Steps to reproduce**:
   - Exact commands run
   - What you expected
   - What actually happened

3. **Logs**:
   - Relevant error messages
   - Stack traces
   - Screenshot if UI issue

4. **What you tried**:
   - Solutions attempted
   - Results of each attempt

## Common Error Messages

### `EADDRINUSE: address already in use`
**Solution**: Another process is using the port. Find and kill it, or use a different port.

### `Cannot find module`
**Solution**: Run `npm install` or `go mod download`

### `permission denied`
**Solution**: Check file permissions or add `sudo` (Linux)

### `dial tcp: connect: connection refused`
**Solution**: Service not running. Start the required service (database, backend, etc.)

### `syntax error at or near`
**Solution**: SQL syntax error. Check query format and PostgreSQL version compatibility.

### `type error` (TypeScript)
**Solution**: Fix type mismatches. Run `npx tsc --noEmit` to see all errors.

## Preventive Measures

### Regular Maintenance

```bash
# Weekly tasks
docker system prune -a  # Clean up Docker
go clean -cache         # Clean Go cache
npm cache clean --force # Clean npm cache

# Update dependencies
cd backend && go get -u ./...
cd frontend && npm update
```

### Backup Important Data

```bash
# Backup database
pg_dump -h localhost -U taskuser -d taskdb > backup.sql

# Backup configuration
cp db/.env db/.env.backup
cp backend/.env backend/.env.backup
```

### Keep Versions Updated

- Go: Check for updates at [go.dev](https://go.dev/dl/)
- Node: Use LTS version from [nodejs.org](https://nodejs.org/)
- Docker: Update Docker Desktop regularly
- Dependencies: Review and update periodically
