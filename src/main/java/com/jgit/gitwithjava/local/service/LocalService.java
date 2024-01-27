package com.jgit.gitwithjava.local.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.frontend.model.FileModel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        return gitServices.getCommits(git);
    }

    public Map<String, String> getAuthors(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        return gitServices.getAuthors(git);
    }

    public Map<Object, Object> getGraph(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        LinkedList<RevCommit> commitLinkedList = new LinkedList<>();
        git.log().call().forEach(commitLinkedList::add);
        Map<Object, Integer> getAllCommitsByDate = getAllCommitsByDate(commitLinkedList);
        Map<String, Object> getLastDayCommits = getLastDayCommits(git);
        Map<String, Integer> getAuthorWithCommitCount = getAuthorWithCommitCount(commitLinkedList);
        return Map.of("commitsByDate", getAllCommitsByDate, "lastDayCommits", getLastDayCommits, "authorCommitsCount", getAuthorWithCommitCount);
    }

    public Map<Object, Integer> getAllCommitsByDate(LinkedList<RevCommit> commitLinkedList) {
        return commitLinkedList.stream().collect(Collectors.groupingBy(revCommit -> {
            Instant getDate = Instant.ofEpochSecond(revCommit.getCommitTime());
            ZonedDateTime firstCommitDate = ZonedDateTime.ofInstant(getDate, ZoneId.systemDefault());
            return firstCommitDate.toLocalDate();
        }, Collectors.summingInt(commit -> 1)));
    }

    public Map<String, Object> getLastDayCommits(Git git) throws GitAPIException {
        LinkedList<RevCommit> commitLinkedList = new LinkedList<>();
        git.log().call().forEach(revCommit -> {
            Instant commitInstant = Instant.ofEpochSecond(revCommit.getCommitTime());
            ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, ZoneId.systemDefault());
            ZonedDateTime firstCommitDate = null;
            if (!commitLinkedList.isEmpty()) {
                Instant getDate = Instant.ofEpochSecond(commitLinkedList.getFirst().getCommitTime());
                firstCommitDate = ZonedDateTime.ofInstant(getDate, ZoneId.systemDefault());
            }
            if (commitLinkedList.isEmpty() || authorDateTime.toLocalDate().equals(firstCommitDate.toLocalDate())) {
                commitLinkedList.add(revCommit);
            }
        });
        Map<String, Integer> allCommits = commitLinkedList.stream().collect(Collectors.groupingBy(commit -> commit.getAuthorIdent().getName(), Collectors.summingInt(commit -> 1)));
        Instant getDate = Instant.ofEpochSecond(commitLinkedList.getFirst().getCommitTime());
        ZonedDateTime firstCommitDate = ZonedDateTime.ofInstant(getDate, ZoneId.systemDefault());
        return Map.of("commits", allCommits, "lastDate", firstCommitDate.toLocalDate());
    }

    public Map<String, Integer> getAuthorWithCommitCount(LinkedList<RevCommit> commitLinkedList) {
        Map<String, Integer> authorCommitCounts = new HashMap<>();
        commitLinkedList.forEach(commit -> {
            String author = commit.getAuthorIdent().getEmailAddress();
            authorCommitCounts.put(author, authorCommitCounts.getOrDefault(author, 0) + 1);
        });
        return authorCommitCounts;
    }

}
