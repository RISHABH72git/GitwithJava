package com.jgit.gitwithjava.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GitCommit {

    private String name;

    private String email;

    private String commitMessage;

    public GitCommit(String name, String email, String commitMessage) {
        this.name = name;
        this.email = email;
        this.commitMessage = commitMessage;
    }
}
