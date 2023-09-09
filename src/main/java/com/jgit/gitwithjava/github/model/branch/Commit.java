package com.jgit.gitwithjava.github.model.branch;

import com.jgit.gitwithjava.github.model.commits.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter @Setter  @ToString
public class Commit {
    public String sha;
    public String node_id;
    public Commit commit;
    public String url;
    public String html_url;
    public String comments_url;
    public Author author;
    public Committer committer;
    public ArrayList<Object> parents;
    public String message;
    public Tree tree;
    public int comment_count;
    public Verification verification;
}
