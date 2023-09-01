package com.jgit.gitwithjava.custom.model;

import com.jgit.gitwithjava.DefaultCredentials;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitRemote {

    private String remoteName;

    private String remoteBranchName;

    private String remoteUrl;

    private String username;

    private String password;

    public GitRemote(String remoteName, String remoteBranchName, String remoteUrl, String username, String password) {
        this.remoteName = remoteName;
        this.remoteBranchName = remoteBranchName;
        this.remoteUrl = remoteUrl;
        this.username = username;
        this.password = password;
    }

    public GitRemote(String remoteName, String remoteUrl, String username, String password) {
        this.remoteName = remoteName;
        this.remoteUrl = remoteUrl;
        this.username = username;
        this.password = password;
    }

    public GitRemote(String remoteUrl) {
        this.remoteName = "origin";
        this.remoteBranchName = "main";
        this.remoteUrl = remoteUrl;
        this.username = DefaultCredentials.getGitUsername();
        this.password = DefaultCredentials.getToken();
    }

    public GitRemote() {
        this.remoteName = "origin";
        this.remoteBranchName = "main";
        this.username = DefaultCredentials.getGitUsername();
        this.password = DefaultCredentials.getToken();
    }
}
