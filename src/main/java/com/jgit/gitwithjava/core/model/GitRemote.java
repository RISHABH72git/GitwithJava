package com.jgit.gitwithjava.core.model;

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

    public GitRemote(String remoteName, String username, String password) {
        this.remoteName = remoteName;
        this.username = username;
        this.password = password;
    }

    public GitRemote(String remoteName, String remoteUrl, String username, String password) {
        this.remoteName = remoteName;
        this.remoteUrl = remoteUrl;
        this.username = username;
        this.password = password;
    }

    public GitRemote(String remoteName, String remoteBranchName, String remoteUrl, String username, String password) {
        this.remoteName = remoteName;
        this.remoteBranchName = remoteBranchName;
        this.remoteUrl = remoteUrl;
        this.username = username;
        this.password = password;
    }
}
