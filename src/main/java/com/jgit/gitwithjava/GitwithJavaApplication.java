package com.jgit.gitwithjava;
import com.jgit.gitwithjava.custom.model.GitClone;
import com.jgit.gitwithjava.custom.model.GitCommit;
import com.jgit.gitwithjava.custom.model.GitRemote;
import com.jgit.gitwithjava.custom.service.GitCustomService;
import com.jgit.gitwithjava.github.model.CreateRepository;
import com.jgit.gitwithjava.github.model.RepoData;
import com.jgit.gitwithjava.github.service.GitHubRestApiService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

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
