package com.jgit.gitwithjava;
import com.jgit.gitwithjava.gitCustom.model.GitCommit;
import com.jgit.gitwithjava.gitCustom.service.GitCustomService;
import com.jgit.gitwithjava.githubApi.model.RepoData;
import com.jgit.gitwithjava.githubApi.service.GitHubRestApiService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
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
        GitCustomService gitCustomService = new GitCustomService();
        Git git = gitCustomService.addAllFile(DefaultCredentials.getRootFolder()+"Videos/springBootProject/GitwithJava");
        gitCustomService.addCommit(git,new GitCommit("rishabh", "rishabh.maurya@ioanyt.com","add model for gitcustom"));
    }

}
