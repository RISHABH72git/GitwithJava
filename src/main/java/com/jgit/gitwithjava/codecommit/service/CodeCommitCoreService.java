package com.jgit.gitwithjava.codecommit.service;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeCommitCoreService {

    public CodeCommitClient getCodeCommitClient(Region region) {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create("default");
        return CodeCommitClient.builder().region(region).credentialsProvider(credentialsProvider).build();
    }

    public Map<String, String> listRepositories(CodeCommitClient codeCommitClient) {
        ListRepositoriesResponse listRepositoriesResult = codeCommitClient.listRepositories();
        Map<String, String> stringStringMap = new HashMap<>();
        if (listRepositoriesResult.hasRepositories()) {
            for (RepositoryNameIdPair repositoryNameIdPair : listRepositoriesResult.repositories()) {
                stringStringMap.put(repositoryNameIdPair.repositoryId(), repositoryNameIdPair.repositoryName());
            }
        }
        return stringStringMap;
    }

    public RepositoryMetadata getRepositoryMetadata(String repoName, CodeCommitClient codeCommitClient) {
        GetRepositoryRequest repositoryRequest = GetRepositoryRequest.builder().repositoryName(repoName).build();
        GetRepositoryResponse repositoryResult = codeCommitClient.getRepository(repositoryRequest);
        return repositoryResult.repositoryMetadata();
    }

    public List<String> listBranches(CodeCommitClient codeCommitClient, String repoName) {
        ListBranchesRequest listBranchesRequest = ListBranchesRequest.builder().repositoryName(repoName).build();
        ListBranchesResponse listBranchesResponse = codeCommitClient.listBranches(listBranchesRequest);
        return listBranchesResponse.branches();
    }

    public BranchInfo getBranches(CodeCommitClient codeCommitClient, String repoName, String branchName) {
        GetBranchRequest getBranchRequest = GetBranchRequest.builder().repositoryName(repoName).branchName(branchName).build();
        GetBranchResponse getBranchResponse = codeCommitClient.getBranch(getBranchRequest);
        return getBranchResponse.branch();
    }

    public Commit getCommit(CodeCommitClient codeCommitClient, String commitId, String repoName) {
        GetCommitRequest getCommitRequest = GetCommitRequest.builder().commitId(commitId).repositoryName(repoName).build();
        GetCommitResponse getCommitResponse = codeCommitClient.getCommit(getCommitRequest);
        return getCommitResponse.commit();
    }

    public List<Difference> getDifference(CodeCommitClient codeCommitClient, String repoName, String commitId, String parentId) {
        GetDifferencesRequest getDifferencesRequest = GetDifferencesRequest.builder().repositoryName(repoName).afterCommitSpecifier(commitId).beforeCommitSpecifier(parentId).build();
        GetDifferencesResponse getDifferencesResponse = codeCommitClient.getDifferences(getDifferencesRequest);
        return getDifferencesResponse.differences();
    }

}
