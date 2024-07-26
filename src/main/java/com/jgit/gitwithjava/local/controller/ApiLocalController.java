package com.jgit.gitwithjava.local.controller;

import com.jgit.gitwithjava.local.service.LocalService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Ref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
    public Map<DiffEntry.ChangeType, List<String>> commitDiffEntry(@RequestParam String path, @PathVariable String commitId) throws IOException {
        return localService.commitDiffEntry(path, commitId);
    }

    @GetMapping("/getFiles")
    public Map<String, Object> getFiles(@RequestParam String path) throws GitAPIException, IOException {
        return localService.getFiles(path);
    }

    @GetMapping("/getDiffByChildCommitAndParentCommit")
    public Map<String, Object> getDiffByChildCommitAndParentCommit(@RequestParam String path, String commitId, String parentCommitId) throws IOException {
        return localService.getDiffByChildCommitAndParentCommit(path, commitId, parentCommitId);
    }

    @GetMapping("/getCommitsByAuthorEmail")
    public List<Object> getCommitsByAuthorEmail(@RequestParam String path, String email) throws GitAPIException, IOException {
        return localService.getCommitsByAuthorEmail(path, email);
    }

    @GetMapping("/getCommitsByBranch")
    public List<Object> getCommitsByBranch(@RequestParam String path, String branch) throws GitAPIException, IOException {
        return localService.getCommitsByBranch(path, branch);
    }

    @GetMapping("/getBlame")
    public List<Map<String, Object>> getBlame(@RequestParam String path, String filename) throws GitAPIException, IOException {
        return localService.getBlame(path, filename);
    }

    @GetMapping("/push")
    public List<Object> push(@RequestParam String path, String name) throws GitAPIException, IOException {
        return localService.push(path, name);
    }

    @GetMapping("/pull")
    public String pull(@RequestParam String path, String name) throws GitAPIException, IOException {
        return localService.pull(path, name);
    }
}
