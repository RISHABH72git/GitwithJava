package com.jgit.gitwithjava.local.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RepoModel {

    private String repoName;

    private String description;

    private boolean access;

    public RepoModel(String repoName, String description, boolean access) {
        this.repoName = repoName;
        this.description = description;
        this.access = access;
    }

    public RepoModel() {
    }

    @Override
    public String toString() {
        return "RepoModel{" +
                "repoName='" + repoName + '\'' +
                ", description='" + description + '\'' +
                ", access='" + access + '\'' +
                '}';
    }
}
