package com.jgit.gitwithjava.githubProjection;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RepoData {

    private String name;

    private String description;

    private boolean isPrivate;

    public RepoData(String name, String description, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
    }
}
