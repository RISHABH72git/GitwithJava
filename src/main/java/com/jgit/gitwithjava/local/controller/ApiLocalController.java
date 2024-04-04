package com.jgit.gitwithjava.local.controller;

import com.jgit.gitwithjava.local.service.LocalService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ApiLocalController {

    LocalService localService;

    @Autowired
    public ApiLocalController(LocalService localService) {
        this.localService = localService;
    }

    @GetMapping("/getCommits")
    public List<Object> getCommits(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getCommits(path);
    }

    @GetMapping("/getAuthors")
    public List<Map<String, String>> getAuthors(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getAuthors(path);
    }

    @GetMapping("/getGraph")
    public Map<Object, Object> getGraph(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getGraph(path);
    }

    @GetMapping("/getStatus")
    public Map<String, Set<String>> getStatus(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getGitStatus(path);
    }

    @GetMapping("/getBranch")
    public List<Map<String, Object>> getBranch(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getBranch(path);
    }

    @GetMapping("/getConfig")
    public Map<String, Object> getConfig(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getConfig(path);
    }

    @GetMapping("/getCommit/{commitId}")
    public Map<String, Object> getCommits(@RequestParam String path, @PathVariable String commitId) throws GitAPIException, IOException {
        return localService.getCommit(path, commitId);
    }

    @GetMapping("/commitDiffEntry/{commitId}")
    public List<Object> commitDiffEntry(@RequestParam String path, @PathVariable String commitId) throws IOException {
        return localService.commitDiffEntry(path, commitId);
    }

    @GetMapping("/getFiles")
    public  Map<String, Object> getFiles(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getFiles(path);
    }
}
