package com.jgit.gitwithjava;
import com.jgit.gitwithjava.custom.service.GitCustomService;
import com.jgit.gitwithjava.github.model.branch.Branch;
import com.jgit.gitwithjava.github.model.branches.Branches;
import com.jgit.gitwithjava.github.service.GitHubRestApiService;
import org.eclipse.jgit.api.Git;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitwithJavaApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GitwithJavaApplication.class, args);
        GitHubRestApiService gitHubRestApiService  = new GitHubRestApiService();
       /* File file = new File(DefaultCredentials.getRootFolder()+"Videos/springBootProject/GitwithJava");
        Git git = Git.open(file);
        GitCustomService gitCustomService = new GitCustomService();
        gitCustomService.gitPush(git);
        git.close();*/
    }
}
