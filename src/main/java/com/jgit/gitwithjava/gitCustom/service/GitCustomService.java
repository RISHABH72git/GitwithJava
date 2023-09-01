package com.jgit.gitwithjava.gitCustom.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.gitCustom.model.GitClone;
import com.jgit.gitwithjava.gitCustom.model.GitCommit;
import com.jgit.gitwithjava.gitCustom.model.GitRemote;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
@Log4j2
public class GitCustomService {

    public Git gitFirstPush(Git git, GitRemote gitRemote) throws IOException, GitAPIException {
        git.remoteAdd().setName(gitRemote.getRemoteName()).setUri(new URIish(new URL(gitRemote.getRemoteUrl()))).call();
        //Rename the branch to 'main'
        git.branchRename().setNewName(gitRemote.getRemoteBranchName()).call();
        // Push the changes to the 'main' branch
        git.push().setRemote(gitRemote.getRemoteName()).setRefSpecs(new RefSpec("refs/heads/" + gitRemote.getRemoteBranchName() + ":refs/heads/" + gitRemote.getRemoteBranchName())).setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRemote.getUsername(), gitRemote.getPassword())).call();
        log.info("First push to origin {}", gitRemote.getRemoteUrl());
        return git;
    }

    public Git gitPush(Git git) throws IOException, GitAPIException {
        GitRemote gitRemote = new GitRemote();
        // Push the changes to the 'main' branch
        git.push().setRemote(gitRemote.getRemoteName()).setRefSpecs(new RefSpec("refs/heads/" + gitRemote.getRemoteBranchName() + ":refs/heads/" + gitRemote.getRemoteBranchName())).setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRemote.getUsername(), gitRemote.getPassword())).call();
        log.info("push to origin {}", gitRemote.getRemoteUrl());
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
}
