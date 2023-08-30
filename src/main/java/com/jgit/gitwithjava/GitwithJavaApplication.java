package com.jgit.gitwithjava;

import com.jgit.gitwithjava.service.GitHubRestApiService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitwithJavaApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GitwithJavaApplication.class, args);
        GitHubRestApiService gitHubRestApiService = new GitHubRestApiService();
    }

}
