package com.jgit.gitwithjava.local.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.frontend.model.FileModel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class LocalService {

    @Autowired
    GitServices gitServices;

    public List<FileModel> localAllFile(String path) {
        File file;
        List<FileModel> allFiles = new ArrayList<>(20);
        if (Objects.isNull(path)) {
            file = new File(DefaultCredentials.getRootFolder());
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (!file1.isHidden() && file1.isDirectory()) {
                    FileModel fileModel = new FileModel();
                    fileModel.setFileName(file1.getName());
                    fileModel.setFileParentName(file1.getParent());
                    fileModel.setIsDirectory(file1.isDirectory());
                    fileModel.setIsHidden(file1.isHidden());
                    boolean gitAvail = false;
                    if (file1.isDirectory()) {
                        gitAvail = Arrays.asList(Objects.requireNonNull(file1.list())).contains(".git");
                    }
                    fileModel.setHasGit(gitAvail);
                    allFiles.add(fileModel);
                }
            }
        } else {
            file = new File(DefaultCredentials.getRootFolder() + path);
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (!file1.isHidden()) {
                    FileModel fileModel = new FileModel();
                    fileModel.setFileName(file1.getName());
                    fileModel.setFileParentName(file1.getParent());
                    fileModel.setIsDirectory(file1.isDirectory());
                    fileModel.setIsHidden(file1.isHidden());
                    boolean gitAvail = false;
                    if (file1.isDirectory()) {
                        gitAvail = Arrays.asList(Objects.requireNonNull(file1.list())).contains(".git");
                    }
                    fileModel.setHasGit(gitAvail);
                    allFiles.add(fileModel);
                }
            }
        }
        return allFiles;
    }

    public List<Object> getCommits(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder()+path));
        return gitServices.getCommits(git);
    }

    public Map<String, String> getAuthors(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder()+path));
        return gitServices.getAuthors(git);
    }

}
