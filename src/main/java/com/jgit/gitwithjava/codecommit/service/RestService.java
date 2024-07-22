package com.jgit.gitwithjava.codecommit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.BranchInfo;
import software.amazon.awssdk.services.codecommit.model.Commit;
import software.amazon.awssdk.services.codecommit.model.RepositoryMetadata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestService {

    @Autowired
    CodeCommitCoreService codeCommitCoreService;

    public List<Object> repositories(Region region) {
        CodeCommitClient codeCommitClient = codeCommitCoreService.getCodeCommitClient(region);
        return codeCommitCoreService.listRepositories(codeCommitClient);
    }

    public Map<String, String> repositoryMetadata(String repoName, Region region) {
        CodeCommitClient codeCommitClient = codeCommitCoreService.getCodeCommitClient(region);
        RepositoryMetadata repositoryMetadata = codeCommitCoreService.getRepositoryMetadata(repoName, codeCommitClient);
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("repositoryId", repositoryMetadata.repositoryId());
        stringStringMap.put("repositoryName", repositoryMetadata.repositoryName());
        stringStringMap.put("repositoryDescription", repositoryMetadata.repositoryDescription());
        stringStringMap.put("accountId", repositoryMetadata.accountId());
        stringStringMap.put("arn", repositoryMetadata.arn());
        stringStringMap.put("cloneUrlHttp", repositoryMetadata.cloneUrlHttp());
        stringStringMap.put("cloneUrlSsh", repositoryMetadata.cloneUrlSsh());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(repositoryMetadata.creationDate(), zoneId);
        String creationDate = localDateTime.format(formatter);
        stringStringMap.put("creationDate", creationDate);
        LocalDateTime lastModifiedDateTime = LocalDateTime.ofInstant(repositoryMetadata.lastModifiedDate(), zoneId);
        String lastModifiedDate = lastModifiedDateTime.format(formatter);
        stringStringMap.put("lastModifiedDate", lastModifiedDate);
        stringStringMap.put("defaultBranch", repositoryMetadata.defaultBranch());
        return stringStringMap;
    }

    public List<String> branches(String repoName, Region region) {
        CodeCommitClient codeCommitClient = codeCommitCoreService.getCodeCommitClient(region);
        return codeCommitCoreService.listBranches(codeCommitClient, repoName);
    }

    public Map<String, String> branchInfo(Region region, String repoName, String branchName) {
        CodeCommitClient codeCommitClient = codeCommitCoreService.getCodeCommitClient(region);
        BranchInfo branchInfo = codeCommitCoreService.getBranches(codeCommitClient, repoName, branchName);
        Map<String, String> stringStringMap = new HashMap<>();
        stringStringMap.put("branchName", branchInfo.branchName());
        stringStringMap.put("commitId", branchInfo.commitId());
        return stringStringMap;
    }

    public Map<String, Object> commitInfo(Region region, String repoName, String branchName, String commitId) {
        CodeCommitClient codeCommitClient = codeCommitCoreService.getCodeCommitClient(region);
        Commit commitInfo = codeCommitCoreService.getCommit(codeCommitClient, commitId, repoName);
        Map<String, Object> stringStringMap = new HashMap<>();
        stringStringMap.put("message", commitInfo.message());
        stringStringMap.put("commitId", commitInfo.commitId());
        stringStringMap.put("additionalData", commitInfo.additionalData());
        stringStringMap.put("treeId", commitInfo.treeId());
        stringStringMap.put("parents", commitInfo.parents());
        stringStringMap.put("committerEmail", commitInfo.committer().email());
        stringStringMap.put("committerName", commitInfo.committer().name());
        stringStringMap.put("committerDate", commitInfo.committer().date());
        return stringStringMap;
    }
}
