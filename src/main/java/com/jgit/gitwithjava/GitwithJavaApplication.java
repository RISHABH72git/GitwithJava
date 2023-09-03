package com.jgit.gitwithjava;
import com.jgit.gitwithjava.custom.model.GitClone;
import com.jgit.gitwithjava.custom.model.GitCommit;
import com.jgit.gitwithjava.custom.service.GitCustomService;
import org.eclipse.jgit.api.Git;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class GitwithJavaApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GitwithJavaApplication.class, args);
    }

}
