package com.jgit.gitwithjava.local.service;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            revCommitList.add(singleCommit);
        }
        return revCommitList;
    }

    public Map<String, String> getAuthors(Git git) throws IOException, GitAPIException {
        Map<String, String> authorName = new HashMap<>();
            git.log().call().forEach(ref -> {
                authorName.put(ref.getCommitterIdent().getName(), ref.getCommitterIdent().getEmailAddress());
            });
        return authorName;
    }
}
