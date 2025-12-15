# Introduction

In this tutorial, we will explore how to delegate tasks to a remote GitHub Copilot Coding Agent. This feature allows you to offload specific tasks to a remote agent. The benefit of a "Copilot remote agent" is its ability to perform autonomous task in the background, without requiring continuous user interaction. 

The Readme.md currently still contains the setup for the Tutorial and not describing the application itself. In this task, we will ask a remote agent to update the Readme.md file to better describe the sample application.

> [!WARNING]
> This task will only work if the repository is owned by a Organization with GitHub Copilot for Business enabled. Therefore the this tutorial will be shown as demo only. 

# Task
- Before you start, make sure you have pushed the latest changes to the remote repository on GitHub.

- In the Agent Sessions of Visual Studio code, click on the button "Start Cloud Agent Session". 

# Additional Context
- The remote agent can be improved by adding a [copilot-setup-steps](https://docs.github.com/en/enterprise-cloud@latest/copilot/how-tos/use-copilot-agents/coding-agent/customize-the-agent-environment#preinstalling-tools-or-dependencies-in-copilots-environment) workflow to the repository. 

- In the Past there was a feature [Copilot Workspace](https://githubnext.com/projects/copilot-workspace) which had similar functionality. This feature has been sunsetted and is no longer available.

# References
[VS Code Docs](https://code.visualstudio.com/docs/copilot/copilot-coding-agent#_assign-work-to-copilot-coding-agent-in-vs-code)