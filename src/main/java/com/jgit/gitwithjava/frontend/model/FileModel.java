package com.jgit.gitwithjava.frontend.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FileModel {

    String fileName;

    String fileParentName;

    Boolean hasGit;

    Boolean isHidden;

    Boolean isDirectory;

    public FileModel(String fileName, String fileParentName, Boolean hasGit, Boolean isHidden, Boolean isDirectory) {
        this.fileName = fileName;
        this.fileParentName = fileParentName;
        this.hasGit = hasGit;
        this.isHidden = isHidden;
        this.isDirectory = isDirectory;
    }

    public FileModel() {
    }
}
