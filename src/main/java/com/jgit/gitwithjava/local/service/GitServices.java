package com.jgit.gitwithjava.local.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.core.model.GitClone;
import com.jgit.gitwithjava.core.model.GitRemote;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.merge.ContentMergeStrategy;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class GitServices {

    public GitServices() {
    }

    public LogCommand commits(Git git) throws IOException, GitAPIException {
        return git.log().all();
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

    public Iterator<RevCommit> getLog(Git git) throws GitAPIException {
        return git.log().call().iterator();
    }

    public Iterator<RevCommit> getLogByBranch(Git git, String branch) throws GitAPIException, IOException {
        return git.log().add(git.getRepository().resolve(branch)).call().iterator();
    }

    public BlameResult getBlameResult(Git git, String filename) throws GitAPIException, IOException {
        BlameResult blameResult = git.blame().setFilePath(filename).setTextComparator(RawTextComparator.DEFAULT).call();
        blameResult.computeAll();
        return blameResult;
    }

    public void add(Git git, List<String> selectedFile) throws GitAPIException {
        AddCommand addCommand = git.add();
        for (String file : selectedFile) {
            addCommand.addFilepattern(file);
        }
        addCommand.call();
    }

    public void remove(Git git, List<String> selectedFile) throws GitAPIException {
        RmCommand rmCommand = git.rm();
        for (String addedFile : selectedFile) {
            rmCommand.addFilepattern(addedFile);
        }
        rmCommand.call();
    }

    public void commit(Git git, List<String> selectedFile, String message) throws GitAPIException {
        if (message != null) {
            CommitCommand commitCommand = git.commit();
            for (String addedFile : selectedFile) {
                commitCommand.setOnly(addedFile);
            }
            commitCommand.setMessage(message).call();
        }
    }

    public void clean(Git git, List<String> untrackedFile) throws GitAPIException {
        CleanCommand cleanCommand = git.clean();
        cleanCommand.setPaths(new HashSet<>(untrackedFile));
        cleanCommand.setCleanDirectories(true);
        cleanCommand.setDryRun(false);
        cleanCommand.call();
    }

    public void restore(Git git, List<String> selectedFile) throws GitAPIException {
        ResetCommand resetCommand = git.reset();
        for (String addedFile : selectedFile) {
            resetCommand.addPath(addedFile);
        }
        resetCommand.call();
    }

    public void checkout(Git git, List<String> selectedFile) throws GitAPIException {
        git.checkout().addPaths(selectedFile).call();
    }

    public void reset(Git git) throws GitAPIException {
        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(Constants.HEAD).call();
    }

    public Ref reset(Git git, String type, String commitId) throws GitAPIException {
        return git.reset().setMode(ResetCommand.ResetType.valueOf(type)).setRef(commitId).call();
    }

    public List<RemoteConfig> remoteConfigList(Git git) throws GitAPIException {
        return git.remoteList().call();
    }

    public Iterable<PushResult> push(Git git, String name) throws GitAPIException {
        return git.push().setRemote(name).setCredentialsProvider(new UsernamePasswordCredentialsProvider(DefaultCredentials.getGitUsername(), DefaultCredentials.getToken())).call();
    }

    public FetchResult fetch(Git git, String name) throws GitAPIException {
        return git.fetch().setRemote(name).setCredentialsProvider(new UsernamePasswordCredentialsProvider(DefaultCredentials.getGitUsername(), DefaultCredentials.getToken())).call();
    }

    public MergeResult merge(Git git, String remoteBranch) throws GitAPIException, IOException {
        return git.merge().include(git.getRepository().findRef(remoteBranch)).setStrategy(MergeStrategy.RECURSIVE).call();
    }

    public PullResult pull(Git git, String name) throws GitAPIException {
        return git.pull().setRemote(name).setStrategy(MergeStrategy.RECURSIVE).call();
    }

    public RevCommit revert(Git git, String commitId) throws IOException, GitAPIException {
        ObjectId objectId = git.getRepository().resolve(commitId);
        return git.revert().include(objectId).call();
    }

    public List<RevCommit> stashList(Git git) throws GitAPIException {
        return new ArrayList<>(git.stashList().call());
    }

    public RevCommit stashCreate(Git git, String indexMessage, String workingDirectoryMessage, Boolean includeUntracked) throws GitAPIException {
        return git.stashCreate().setIncludeUntracked(includeUntracked).setIndexMessage(indexMessage).setWorkingDirectoryMessage(workingDirectoryMessage).call();
    }

    public void stashApply(Git git, String stashId) throws GitAPIException {
        git.stashApply().setContentMergeStrategy(ContentMergeStrategy.CONFLICT).setStashRef(stashId).call();
    }
}
