package com.jgit.gitwithjava.local.service;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
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
}
