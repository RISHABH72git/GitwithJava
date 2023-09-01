package com.jgit.gitwithjava.github.model.commits;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter @Setter @ToString
public class GetCommit {
    public String url;
    public String sha;
    public String node_id;
    public String html_url;
    public String comments_url;
    public Commit commit;
    public Author author;
    public Committer committer;
    public ArrayList<Parent> parents;
}
