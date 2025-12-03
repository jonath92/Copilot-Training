# Introduction
In modern Software Development, Continuous Integration and Continuous Deployment (CI/CD) are essential practices that help teams deliver high-quality software quickly and reliably. By automating the build, test, and deployment processes, CI/CD pipelines ensure that code changes are integrated smoothly and deployed to production with minimal manual intervention.

# Task 
In the `.github/workflows` directory, you already find a `ci_cd_db.yaml` GitHub Actions workflow file. This workflow is intended to check the database setup as part of the CI/CD process. For simplicity, we skip pushing the database as a Docker image to a container registry and only test the docker build process.

- This time switch to the "Edit" mode in the Copilot Chat.

> Edit mode is perfect when you know what you want to do but don’t necessarily want to write it all out yourself. You highlight a block of code, type in an instruction—perhaps something like “add error handling” or “refactor this using async/await”—and Copilot rewrites the code for you. But (and this is important) it doesn’t save anything without showing you the diff first. [source](https://github.blog/ai-and-ml/github-copilot/copilot-ask-edit-and-agent-modes-what-they-do-and-when-to-use-them/)

- Go to the `ci_cd_db.yaml` file and select the area where the docker build should be implemented.
- Ask Copilot in the "Edit" mode to implement the docker build for the database service. Hint: Copilot might want to push the image to a container registry, which we do not want in this case. Undo the suggestion and update your prompt to ask Copilot to only implement the build process without pushing to a registry.
- Commit and push your changes to your remote repository and test if the workflow runs successfully.