package com.jgit.gitwithjava.github.model.branches;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Commit {
    public String sha;
    public String url;
}
