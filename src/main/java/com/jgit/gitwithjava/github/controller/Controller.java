package com.jgit.gitwithjava.github.controller;

import com.jgit.gitwithjava.github.model.ListRepository;
import com.jgit.gitwithjava.github.model.RepoData;
import com.jgit.gitwithjava.github.service.GitHubRestApiService;
import com.jgit.gitwithjava.local.model.RepoModel;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@org.springframework.stereotype.Controller
@RequestMapping("github")
public class Controller {

    @Autowired
    GitHubRestApiService gitHubRestApiService;

    @GetMapping("")
    public String localAllFile(Model model) throws Exception {
        if (gitHubRestApiService.getUsers().toString().startsWith("401 Unauthorized:")) {
            model.addAttribute("error", gitHubRestApiService.getUsers());
            return "GitHub/error";
        }
        model.addAttribute("userDetails", gitHubRestApiService.getUsers());
        ListRepository[] allRepos = gitHubRestApiService.listAllRepository();
        model.addAttribute("counts", gitHubRestApiService.getCountOfRepos(allRepos));
        model.addAttribute("listAllRepository", allRepos);
        return "GitHub/github";
    }

    @GetMapping("/createRepositoryForm")
    public String githubForm(Model model) {
        model.addAttribute("repoModel", new RepoModel());
        return "GitHub/createRepositoryForm";
    }

    @PostMapping("/createRepository")
    public String createGitHubRepo(Model model, RepoModel repoModel) throws Exception {
        gitHubRestApiService.createRepository(new RepoData(repoModel.getRepoName(), repoModel.getDescription(), repoModel.isAccess()));
        model.addAttribute("listAllRepository", gitHubRestApiService.listAllRepository());
        return "GitHub/github";
    }

    @GetMapping("/branches")
    public String githubForm(Model model, @RequestParam String repoName) {
        model.addAttribute("path", "path");
        model.addAttribute("repoName", repoName);
        model.addAttribute("AllBranch", gitHubRestApiService.getAllBranchesByRepo(repoName));
        return "GitHub/branches";
    }

    @GetMapping("/commits")
    public String githubAllCommits(Model model, @RequestParam String repoName) {
        model.addAttribute("repoName", repoName);
        model.addAttribute("AllCommits", gitHubRestApiService.getCommitsOfRepo(repoName));
        return "GitHub/commits";
    }

    @PostMapping("/downloadCommits")
    public String githubPrintCommits(Model model, @RequestParam String repoName, String fileName, boolean timestamp, boolean message, boolean email) throws IOException, GitAPIException {
        model.addAttribute("repoName", repoName);
        model.addAttribute("AllCommits", gitHubRestApiService.githubPrintCommits(repoName, fileName, timestamp, message, email));
        return "GitHub/commits";
    }

    @GetMapping("/contributors")
    public String githubAllContributors(Model model, @RequestParam String repoName) {
        model.addAttribute("repoName", repoName);
        model.addAttribute("allContributors", gitHubRestApiService.getReposContributions(repoName));
        return "GitHub/contributors";
    }

    @GetMapping("/activities")
    public String githubAllActivities(Model model, @RequestParam String repoName) {
        model.addAttribute("repoName", repoName);
        model.addAttribute("allActivities", gitHubRestApiService.getReposActivity(repoName));
        return "GitHub/activities";
    }

    @PostMapping("/delete")
    public RedirectView deleteGitHubRepo(@RequestParam String repoName) throws IOException, GitAPIException {
        gitHubRestApiService.deleteRepository(repoName);
        return new RedirectView("github");
    }
}
