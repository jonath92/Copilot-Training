# Introduction
We have a database docker container and now we want to add a backend service that interacts with the database. We do this by following the [Api first approach](https://swagger.io/resources/articles/adopting-an-api-first-approach/). 

The default vscode copilot chat has the limitation that you only can have one session at a time. This makes it hard to work on multiple components of the application in parallel. In this task, we will explore how to use the Agent Sessions feature to work on multiple components in parallel.

# Task
- In the Agent Sessions of Visual Studio code, click on the plus icon next to the "Local Chat Agent" 

- In the new windows select the Agent mode and aks GitHub Copilot to create an openapi specification in yaml format for a service that allows CRUD operations to manage tasks with the following fields: ID, Title, Description, Duration. The api.yaml should be stored in the `api-spec` folder.

- Without waiting for Copilot to finish, create a new Agent Session. This time ask Copilot to setup a new backend service in the `backend` folder using a technology stack of your choice (e.g., Go, Node.js with Express, Python with Flask, etc.). For now the backend service should just expose a single endpoint `/health` that returns the status of the service.

- You can now switch between the two Agent Sessions by clicking on them in the Agent Sessions pane. Validate the result of both sessions and keep the changes or instruct Copilot to adjust the code until you are satisfied with the result. 

- Once both components are done, ask copilot to create a script in the language of your choice (bash, powershell, ...) that auto generates the interfaces and models for the backend service based on the openapi specification. The script should be also stored in the `api-spec` folder.

> [!Warning]
> Experience have shown that copilot often produces poor results in this task. You might want to use the GitHub MCP server to find Tools that can help to generate the code from the open-api specification and than guide copilot in the right direction. In that case it might also be helpful to start creating the script on your own and use copilot autocompletion to finish the task.

- Finally, ask copilot to update the code in the backend service to store the data in the database container that we created in a previous tutorial.

Bonus: 
- Create a `docker-compose.yml` file that starts both the database and the backend service and ensures that the backend service waits for the database to be ready before starting. The file should be stored in the root folder of the project.
- GitHub provides public Repositories [Dependabot](https://docs.github.com/en/code-security/getting-started/dependabot-quickstart-guide) for free that can help to keep your dependencies up to date. Ask copilot to create a dependabot configuration file that checks for updates in the backend service once a week.
