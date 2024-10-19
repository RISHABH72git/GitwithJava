# GitWithJava

GitWithJava is a Java-based application that uses the JGit library to manage and explore Git repositories locally. It provides a detailed overview of repository metadata such as commits, branches, authors, blame information, and files, with an integration of GitHub's API. The project is built with Spring Boot to enable web-based interaction with Git repositories.

## Overview

GitWithJava is designed to provide a complete solution for managing and viewing the details of Git repositories. By leveraging the JGit library, the application can:

- List all repositories in your local machine.
- Read and display commit history.
- Manage branches and show branch information.
- Retrieve and show commit author details.
- Display file blame information to track changes made by different authors.
- Access detailed file structure and changes within a repository.
- Integrate with GitHub API for accessing additional details from remote repositories.

The backend is developed using Spring Boot to offer a RESTful web interface, making it easy to integrate with other applications or access via web clients.

## Features

1. **Local Repository Management**
  - Automatically detects and lists all local Git repositories.
  - Provides the ability to explore the contents of each repository.

2. **Commit Details**
  - View detailed commit history, including commit messages, timestamps, and author information.
  - Filter commits by branch or date.

3. **Branch Management**
  - List all branches in a repository.
  - View commit history specific to a branch.

4. **Author & Blame Information**
  - Track who made specific changes to files.
  - Display blame information for each line of a file, helping to identify code authorship.

5. **File Exploration**
  - Access the file structure of the repository.
  - View file contents and differences across commits.

6. **GitHub API Integration**
  - Fetch additional repository information from GitHub, such as pull requests, issues, and contributors.

7. **Spring Boot Web Interface**
  - RESTful API to access repository data.
  - Simple web interface to view repository metadata, commits, branches, and files.

## Technologies Used

- **Java**: Core programming language.
- **JGit**: Java implementation of Git, used for local repository management and Git operations.
- **Spring Boot**: Framework used to create the web application and REST APIs.
- **GitHub API**: For fetching remote repository information.