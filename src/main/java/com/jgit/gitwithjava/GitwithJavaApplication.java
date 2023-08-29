package com.jgit.gitwithjava;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


@SpringBootApplication
public class GitwithJavaApplication {

    public static void main(String[] args) throws IOException, GitAPIException {
        SpringApplication.run(GitwithJavaApplication.class, args);
    }

}
