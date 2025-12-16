## Plan: Implement React Frontend with Mock Data

Create a React + Tailwind CSS + Zustand frontend for the task estimation app. Generate a typed API client from the OpenAPI spec using openapi-typescript-codegen, with an environment variable to toggle between mock and real API.

### Steps

1. **Initialize React project** in `frontend/` using Vite with TypeScript, then install dependencies:
   - `react-router-dom`, `zustand`, `tailwindcss`, `postcss`, `autoprefixer`
   - `openapi-typescript-codegen` (dev dependency)

2. **Generate API client** by running `npx openapi-typescript-codegen --input ../api-spec/openapi.yaml --output src/api --client fetch` to create typed services and models in `src/api/`.

3. **Configure Tailwind CSS** - create `tailwind.config.js` and `postcss.config.js`, add Tailwind directives to `src/index.css`.

4. **Create environment config** - add `.env` with `VITE_USE_MOCK_API=true` and create `src/config.ts` to export the flag for runtime checks.

5. **Create mock service** in `src/services/mockTaskService.ts` - implement mock CRUD operations with sample Task data matching the generated API types.

6. **Create API adapter** in `src/services/taskService.ts` - export functions that check `VITE_USE_MOCK_API` and route to either the mock service or the generated API client.

7. **Create Zustand store** in `src/store/taskStore.ts` - manage tasks state with actions (`fetchTasks`, `addTask`, `updateTask`, `deleteTask`) calling the adapter.

8. **Build UI components** in `src/components/`:
   - `Layout.tsx` - page wrapper with header and nav links
   - `TaskList.tsx` - fetch and display all tasks from store
   - `TaskCard.tsx` - task display with edit/delete actions
   - `TaskForm.tsx` - create/edit form using native HTML5 `required`, `min`, `maxLength` attributes

9. **Set up routing** in `src/App.tsx` - configure React Router with routes: `/` (list), `/tasks/new` (create), `/tasks/:id/edit` (edit).
