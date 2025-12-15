# Introduction
GitHub Advanced Security (GHAS) provides powerful security features to help you identify and remediate vulnerabilities in your code. In combination with GitHub Copilot, you can leverage AI to assist you in fixing security issues more efficiently.

# Task 
- Create a fork of the [demo-python](https://github.com/advanced-security-demo/demo-python) repository to your own GitHub account. 
- Go to --> Settings --> Advanced Security and Setup Code-Scanning. Double check that "Copilot Autofix" is enabled. 

![advanced security 01](./screenshots/advanced_security_01.png)

![advanced security 02](./screenshots/advanced_security_02.png)

- Go to the "Actions" tab and wait for the "CodeQL Setup" workflow to finish. This might take a couple of minutes.

![advanced security 03](./screenshots/advanced_security_03.png)

- Once the workflow is finished, go to the "Security" tab and then to "Code scanning alerts". You should see a list of security issues detected in the codebase.

![advanced security 04](./screenshots/advanced_security_04.png)

- Then click on "Generate Fix" 

![advanced security 05](./screenshots/advanced_security_05.png)