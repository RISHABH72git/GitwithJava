package com.jgit.gitwithjava.custom.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.custom.model.GitClone;
import com.jgit.gitwithjava.custom.model.GitCommit;
import com.jgit.gitwithjava.custom.model.GitRemote;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.merge.ContentMergeStrategy;
import org.eclipse.jgit.transport.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@Log4j2
public class GitCustomService {

    public GitCustomService() {
    }

    public List<RemoteConfig> getGitRemoteList(Git git) throws GitAPIException {
        if (!git.remoteList().call().get(0).getName().equals("origin")) {
            log.error("origin not found");
        }
        return git.remoteList().call();
    }

    public Git gitFirstPush(Git git, GitRemote gitRemote) throws IOException, GitAPIException {
        git.remoteRemove().call();
        git.remoteAdd().setName(gitRemote.getRemoteName()).setUri(new URIish(new URL(gitRemote.getRemoteUrl()))).call();
        git.push().setRemote(gitRemote.getRemoteName()).setRefSpecs(new RefSpec("refs/heads/" + gitRemote.getRemoteBranchName() + ":refs/heads/" + gitRemote.getRemoteBranchName())).setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRemote.getUsername(), gitRemote.getPassword())).call();
        log.info("First push to origin {}", gitRemote.getRemoteUrl());
        return git;
    }

    public Git gitPush(Git git) throws IOException, GitAPIException {
        List<RemoteConfig> list = getGitRemoteList(git);
        if (list.size() == 1) {
            System.out.println(list.get(0).getPushURIs());
            // Push the changes to the remote branch
            GitRemote gitRemote = new GitRemote(list.get(0).getName(), DefaultCredentials.getGitUsername(), DefaultCredentials.getToken());
            git.push().setRemote(gitRemote.getRemoteName()).setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRemote.getUsername(), gitRemote.getPassword())).call();
            log.info("push to origin");
        } else {
            log.info("remote size multiple found");
        }
        return git;
    }

    public String getFilepathByGitClone(GitClone gitClone) throws GitAPIException {
        // Clone the repository
        File file = new File(gitClone.getFilePath());
        Git.cloneRepository().setURI(gitClone.getRemoteUrl()).setDirectory(file)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitClone.getUsername(), gitClone.getPassword())).call();
        log.info("clone repository {}", file.getName());
        return gitClone.getFilePath();
    }

    public Git addAllFile(String filePath) throws IOException, GitAPIException {
        File file = new File(filePath);
        Git git = Git.open(file);
        git.add().addFilepattern(".").call();
        log.info("all file added in {}", file.getName());
        return git;
    }

    public Git addCommit(Git git, GitCommit gitCommit) throws GitAPIException {
        CommitCommand commitCommand = git.commit();
        commitCommand.setMessage(gitCommit.getCommitMessage());
        commitCommand.setAuthor(gitCommit.getName(), gitCommit.getEmail());
        commitCommand.call();
        log.info("commit by {}", gitCommit.getEmail());
        return git;
    }

    public void gitPull(Git git) throws GitAPIException {
        PullCommand pullCommand = git.pull().setContentMergeStrategy(ContentMergeStrategy.CONFLICT).setRemote(git.remoteList().call().get(0).getName()).setCredentialsProvider(new UsernamePasswordCredentialsProvider(DefaultCredentials.getGitUsername(), DefaultCredentials.getToken()));
        PullResult pullResult = pullCommand.call();
        MergeResult mergeResult = pullResult.getMergeResult();
        log.info("MergeStatus is Successful {}",mergeResult.getMergeStatus().isSuccessful());
        FetchResult fetchResult = pullResult.getFetchResult();
        log.info("Fetching from {}",fetchResult.getURI());
        log.info("FetchResult Message : {}",fetchResult.getMessages());
        log.info(fetchResult.getPeerUserAgent());
    }
}
