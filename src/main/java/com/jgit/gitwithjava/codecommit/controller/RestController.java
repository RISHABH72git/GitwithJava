package com.jgit.gitwithjava.codecommit.controller;

import com.jgit.gitwithjava.codecommit.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import software.amazon.awssdk.regions.Region;

import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("CodeCommit")
public class RestController {

    RestService restService;

    @Autowired
    public RestController(RestService restService) {
        this.restService = restService;
    }

    @GetMapping("repositories")
    public List<Object> repositories(String region) {
        return restService.repositories(Region.of(region));
    }

    @GetMapping("repositories/{repoName}")
    public Map<String, String> repositoryMetadata(@PathVariable String repoName, String region) {
        return restService.repositoryMetadata(repoName, Region.of(region));
    }

    @GetMapping("repositories/{repoName}/branches")
    public List<String> branches(@PathVariable String repoName, String region) {
        return restService.branches(repoName, Region.of(region));
    }

    @GetMapping("repositories/{repoName}/branches/{branchName}")
    public Map<String, String> branchInfo(@PathVariable String repoName, String region, @PathVariable String branchName) {
        return restService.branchInfo(Region.of(region), repoName, branchName);
    }

    @GetMapping("repositories/{repoName}/branches/{branchName}/commits/{commitId}")
    public Map<String, Object> branchInfo(@PathVariable String repoName, String region, @PathVariable String branchName, @PathVariable String commitId) {
        return restService.commitInfo(Region.of(region), repoName, branchName, commitId);
    }

}
