package com.jgit.gitwithjava.local.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.custom.model.GitClone;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.IO;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Log4j2
@Service
public class GitServices {

    public GitServices() {
    }

    public List<Object> getCommits(Git git) throws IOException, GitAPIException {
        List<Object> revCommitList = new ArrayList<>();
        LogCommand logCommand = git.log().all();
        for (RevCommit revCommit : logCommand.call()) {
            Map<String, Object> singleCommit = new HashMap();
            singleCommit.put("commitId", revCommit.name());
            singleCommit.put("parentCount", revCommit.getParentCount());
            singleCommit.put("email", revCommit.getCommitterIdent().getEmailAddress());
            singleCommit.put("name", revCommit.getCommitterIdent().getName());
            singleCommit.put("timestamp", revCommit.getCommitTime());
            revCommitList.add(singleCommit);
        }
        return revCommitList;
    }

    public List<Map<String, String>> getAuthors(Git git) throws GitAPIException {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> authorName = new HashMap<>();
        git.log().call().forEach(ref -> {
            authorName.put(ref.getCommitterIdent().getEmailAddress(), ref.getCommitterIdent().getName());
        });
        authorName.forEach((email, name) -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("email", email);
            list.add(map);
        });
        return list;
    }

    public Status getStatus(Git git) throws GitAPIException {
        return git.status().call();
    }

    public List<Ref> getBranch(Git git) throws GitAPIException {
        return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
    }

    public StoredConfig getConfig(Git git) {
        return git.getRepository().getConfig();
    }

    public RevCommit getCommit(Git git, String commitId) throws IOException {
        ObjectId objectId = git.getRepository().resolve(commitId);
        return git.getRepository().parseCommit(objectId);
    }

    public List<DiffEntry> commitDiffEntry(Git git, RevCommit commit) throws IOException {
        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(git.getRepository());
        diffFormatter.setDetectRenames(true);
        return diffFormatter.scan(commit.getParent(0).getId(), commit.getId());
    }

    public void cloneRepository(GitClone gitClone) throws GitAPIException {
        // Clone the repository
        File file = new File(gitClone.getFilePath());
        Git.cloneRepository().setURI(gitClone.getRemoteUrl()).setDirectory(file)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitClone.getUsername(), gitClone.getPassword())).call();
        log.info("clone repository {}", file.getName());
    }

}
