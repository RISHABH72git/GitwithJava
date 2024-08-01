package com.jgit.gitwithjava;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Log4j2
@UtilityClass
public class DefaultCredentials {

    public String getToken() {
        String key = "";
        try {
            key = Files.readString(new File(".apiToken").toPath());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return key;
    }

    public String getGitUsername() {
        String username = "";
        try {
            username = Files.readString(new File(".gitUser").toPath());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return username;
    }

    public String getRootFolder() {
        String root = "";
        try {
            root = Files.readString(new File(".rootFolder").toPath());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return root;
    }

    public String getApplicationFile() {
        String rootFile = DefaultCredentials.getRootFolder();
        String[] rootSplit = rootFile.split(File.separator);
        return rootFile + "." + rootSplit[2] + File.separator + "application.xml";
    }

    public String getApplicationFolder() {
        String rootFile = DefaultCredentials.getRootFolder();
        String[] rootSplit = rootFile.split(File.separator);
        return rootFile + "." + rootSplit[2];
    }
}
