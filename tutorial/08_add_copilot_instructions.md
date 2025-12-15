# Introduction
Copilot custom instructions allow you to provide context and set rules for the AI to follow, improving the relevance and consistency of its response

> [!Note]
> Due to the non-deterministic nature of AI, Copilot may not always follow your custom instructions in exactly the same way every time they are used.

In this task, we will create a default custom instruction file that Copilot will always use and additional a specific instruction for the backend service we will implement in the next task.

- # Task
- Create a new file `.github/copilot-instructions.md`. Best practises on how to write custom instructions, can be found [here](https://docs.github.com/en/copilot/concepts/prompting/response-customization#writing-effective-custom-instructions-1). 

- Create a new folder `instrcutions` within the `.github` folder and a file `.github/instructions/backend-copilot.instructions.md` with specific instructions for the backend service. The technology stack for the backend is up to you. Hint: You might find a useful starting point in the [Awesome GitHub Copilot Customizations Repository](https://github.com/github/awesome-copilot/tree/main/instructions)

- Bonus: GitHub Copilot additionally supports [Prompt Files](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions?tool=vscode#enabling-and-using-prompt-files) that can be used to provide specific instructions for specific tasks. You can find the information how to set it up [here](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions?tool=vscode#enabling-and-using-prompt-files). As bonus task: Create a prompt file to optimize generation of sql files. An example for PostgreSQL can be found [here](https://github.com/github/awesome-copilot/blob/main/prompts/postgresql-optimization.prompt.md). 


# Tips and Tricks
- Copilot also supports `AGENTS.md` files, which is an [open format](https://agents.md/) to guide coding agents plus `CLAUDE.md` and `GEMINI.md` files. See [docs](https://docs.github.com/en/enterprise-cloud@latest/copilot/tutorials/coding-agent/get-the-best-results) for more informations. For this tutorial, we have chosen to use the `copilot-instructions.md` file as this in the moment is the default way to provide custom instructions to Copilot. Due to the open format of `AGENTS.md` files, there is hower a good chance that `AGENTS.md` files will become the standard way to provide custom instructions in the future.