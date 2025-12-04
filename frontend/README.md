# Frontend Documentation

The Task Management Application frontend is built with Vite and TypeScript, providing a fast development experience with modern tooling.

## Technology Stack

- **Build Tool**: Vite 7.2.4
- **Language**: TypeScript 5.9.3
- **Module System**: ES Modules
- **Package Manager**: npm

## Project Structure

```
frontend/
├── index.html          # Entry HTML file
├── package.json        # Dependencies and scripts
├── tsconfig.json       # TypeScript configuration
├── public/             # Static assets
└── src/                # Source code
    ├── main.ts         # Application entry point
    ├── counter.ts      # Example functionality
    ├── style.css       # Global styles
    └── typescript.svg  # Assets
```

## Getting Started

### Installation

```bash
cd frontend
npm install
```

### Development

Start the development server with hot module replacement:

```bash
npm run dev
```

The application will be available at `http://localhost:5173` (or another port if 5173 is in use).

### Building for Production

Compile TypeScript and bundle for production:

```bash
npm run build
```

Output will be in the `dist/` directory.

### Preview Production Build

Preview the production build locally:

```bash
npm run preview
```

## Scripts Reference

| Script | Command | Description |
|--------|---------|-------------|
| `dev` | `vite` | Start development server |
| `build` | `tsc && vite build` | Build for production |
| `preview` | `vite preview` | Preview production build |

## Configuration

### TypeScript (tsconfig.json)

The TypeScript configuration ensures type safety and modern JavaScript features:

- **Target**: ES2020
- **Module**: ES2020
- **Strict Mode**: Enabled
- **Module Resolution**: Bundler

### Vite

Vite provides:
- Fast Hot Module Replacement (HMR)
- Optimized production builds
- Built-in TypeScript support
- Tree-shaking and code splitting

## Development Workflow

### Adding Features

1. Create new TypeScript files in `src/`
2. Import in `main.ts` or other files
3. Vite will automatically reload changes

### Styling

- Global styles: `src/style.css`
- Component styles: Create `.css` files and import in TypeScript

### Type Safety

TypeScript provides compile-time type checking:

```typescript
// Type-safe task interface
interface Task {
  id: string;
  title: string;
  description?: string;
  duration: number;
  status: 'pending' | 'in_progress' | 'completed' | 'cancelled' | 'archived';
  priority: 'low' | 'medium' | 'high' | 'urgent';
}

// Type-safe API call
async function createTask(task: Omit<Task, 'id'>): Promise<Task> {
  const response = await fetch('/api/v1/tasks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task)
  });
  return response.json();
}
```

## API Integration

### Base URL Configuration

Recommended approach using environment variables:

```typescript
// src/config.ts
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
```

### API Client Example

```typescript
// src/api/tasks.ts
import { API_BASE_URL } from '../config';

export interface Task {
  id: string;
  title: string;
  description?: string;
  duration: number;
  status: string;
  priority: string;
  tags?: string[];
  created_at: string;
  updated_at: string;
}

export async function getTasks(limit = 20, offset = 0): Promise<Task[]> {
  const response = await fetch(
    `${API_BASE_URL}/tasks?limit=${limit}&offset=${offset}`
  );
  
  if (!response.ok) {
    throw new Error(`Failed to fetch tasks: ${response.statusText}`);
  }
  
  const data = await response.json();
  return data.tasks;
}

export async function createTask(task: Partial<Task>): Promise<Task> {
  const response = await fetch(`${API_BASE_URL}/tasks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task)
  });
  
  if (!response.ok) {
    throw new Error(`Failed to create task: ${response.statusText}`);
  }
  
  return response.json();
}
```

## State Management

### Current State
The application currently uses vanilla JavaScript without a state management library.

### Recommended Enhancements

For complex applications, consider:
- **Zustand**: Lightweight state management
- **Redux Toolkit**: Full-featured state management
- **React Context**: If migrating to React

Example with Zustand:

```typescript
import create from 'zustand';

interface TaskStore {
  tasks: Task[];
  loading: boolean;
  error: string | null;
  fetchTasks: () => Promise<void>;
  addTask: (task: Partial<Task>) => Promise<void>;
}

export const useTaskStore = create<TaskStore>((set) => ({
  tasks: [],
  loading: false,
  error: null,
  
  fetchTasks: async () => {
    set({ loading: true, error: null });
    try {
      const tasks = await getTasks();
      set({ tasks, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  },
  
  addTask: async (task) => {
    set({ loading: true, error: null });
    try {
      const newTask = await createTask(task);
      set((state) => ({ 
        tasks: [...state.tasks, newTask],
        loading: false 
      }));
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  }
}));
```

## UI Components

### Recommended Framework Migration

Consider migrating to a component framework:

**React**:
```bash
npm install react react-dom
npm install -D @types/react @types/react-dom
```

**Vue 3**:
```bash
npm install vue
```

**Svelte**:
```bash
npm install svelte
```

## Environment Variables

Vite exposes environment variables prefixed with `VITE_`:

**.env.development**:
```
VITE_API_URL=http://localhost:8080/api/v1
```

**.env.production**:
```
VITE_API_URL=https://api.production.com/api/v1
```

Access in code:
```typescript
const apiUrl = import.meta.env.VITE_API_URL;
```

## Testing

### Recommended Setup

**Vitest** (recommended for Vite projects):
```bash
npm install -D vitest @vitest/ui
```

**Configuration** (vite.config.ts):
```typescript
import { defineConfig } from 'vite';

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom'
  }
});
```

**Example Test**:
```typescript
import { describe, it, expect } from 'vitest';
import { getTasks } from './api/tasks';

describe('getTasks', () => {
  it('fetches tasks from API', async () => {
    const tasks = await getTasks(10, 0);
    expect(Array.isArray(tasks)).toBe(true);
  });
});
```

## Performance Optimization

### Code Splitting

Vite automatically splits code by route:

```typescript
// Lazy load heavy components
const HeavyComponent = () => import('./components/HeavyComponent');
```

### Asset Optimization

Vite automatically optimizes:
- Images (WebP conversion)
- CSS (minification)
- JavaScript (tree-shaking)

### Caching Strategy

Configure cache headers in production server:
```
Cache-Control: max-age=31536000, immutable  # For hashed assets
Cache-Control: no-cache                     # For index.html
```

## Deployment

### Build Output

After running `npm run build`, the `dist/` directory contains:
```
dist/
├── index.html
├── assets/
│   ├── index-[hash].js
│   ├── index-[hash].css
│   └── [asset]-[hash].[ext]
```

### Static Hosting

Deploy to:
- **Netlify**: `netlify deploy --prod --dir=dist`
- **Vercel**: `vercel --prod`
- **GitHub Pages**: Configure workflow
- **AWS S3**: `aws s3 sync dist/ s3://bucket-name`

### Docker Deployment

**Dockerfile**:
```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## Troubleshooting

### Port Already in Use

```bash
# Use different port
npm run dev -- --port 3000
```

### Build Failures

```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
npm run build
```

### TypeScript Errors

```bash
# Check TypeScript compilation
npx tsc --noEmit
```

### Module Resolution Issues

Ensure `tsconfig.json` has correct paths:
```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

## Future Enhancements

### Planned Features
- Migration to React or Vue
- Component library integration
- End-to-end testing with Playwright
- Progressive Web App (PWA) support
- Internationalization (i18n)
- Dark mode support
- Accessibility improvements (WCAG 2.1 AA)

### Recommended Libraries
- **UI Framework**: React, Vue, or Svelte
- **Component Library**: Material-UI, Ant Design, or Shadcn UI
- **Forms**: React Hook Form or Formik
- **Data Fetching**: TanStack Query (React Query)
- **Routing**: React Router or Vue Router
- **Testing**: Vitest + Testing Library

## Related Documentation

- [System Architecture](../architecture/system-architecture.md) - Overall system design
- [API Documentation](../api/README.md) - Backend API reference
- [Getting Started Guide](../guides/getting-started.md) - Setup instructions
- [Vite Documentation](https://vitejs.dev) - Official Vite docs
- [TypeScript Handbook](https://www.typescriptlang.org/docs/) - TypeScript guide
