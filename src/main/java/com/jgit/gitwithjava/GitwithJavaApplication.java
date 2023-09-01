package com.jgit.gitwithjava;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitwithJavaApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GitwithJavaApplication.class, args);
//        GitHubRestApiService gitHubRestApiService = new GitHubRestApiService();
//        gitHubRestApiService.deleteRepository("rishabh-ioanyt","new-git-hello");
        //gitHubRestApiService.createRepository(new RepoData("new-git-hello","all git service", false));
//        GitCustomService customService = new GitCustomService();
        //customService.gitPush();
    }

}
