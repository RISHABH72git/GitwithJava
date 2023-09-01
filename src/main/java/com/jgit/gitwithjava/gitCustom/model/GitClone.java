package com.jgit.gitwithjava.gitCustom.model;

import com.jgit.gitwithjava.DefaultCredentials;
import lombok.Getter;

@Getter
public class GitClone {

    private String remoteUrl;

    private String username;

    private String password;

    private String filePath;

    public GitClone(String remoteUrl, String username, String password, String filePath) {
        this.remoteUrl = remoteUrl;
        this.username = username;
        this.password = password;
        this.filePath = filePath;
    }

    public GitClone() {
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFilePath(String filePath) {
        this.filePath = DefaultCredentials.getRootFolder()+filePath;
    }
}
