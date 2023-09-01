package com.jgit.gitwithjava.githubProjection.commits;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Commit {
    public String url;
    public Author author;
    public Committer committer;
    public String message;
    public Tree tree;
    public int comment_count;
    public Verification verification;
}
