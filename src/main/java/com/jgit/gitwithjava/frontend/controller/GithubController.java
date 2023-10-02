package com.jgit.gitwithjava.frontend.controller;

import com.jgit.gitwithjava.frontend.model.RepoModel;
import com.jgit.gitwithjava.github.model.ListRepository;
import com.jgit.gitwithjava.github.model.RepoData;
import com.jgit.gitwithjava.github.service.GitHubRestApiService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
public class GithubController {

    @Autowired
    GitHubRestApiService gitHubRestApiService;

    @GetMapping("/githubRepositories")
    public String githubRepositories(Model model) throws Exception {
        ListRepository[] allRepos = gitHubRestApiService.listAllRepository();
        model.addAttribute("userDetails", gitHubRestApiService.getUsers());
        model.addAttribute("counts", gitHubRestApiService.getCountOfRepos(allRepos));
        model.addAttribute("listAllRepository", allRepos);
        return "githubRepo";
    }

    @GetMapping("/githubForm")
    public String githubForm(Model model) {
        model.addAttribute("repoModel", new RepoModel());
        return "githubForm";
    }

    @PostMapping("/createGitHubRepo")
    public String createGitHubRepo(Model model, RepoModel repoModel) throws Exception {
        gitHubRestApiService.createRepository(new RepoData(repoModel.getRepoName(), repoModel.getDescription(), repoModel.isAccess()));
        model.addAttribute("listAllRepository", gitHubRestApiService.listAllRepository());
        return "githubRepo";
    }

    @PostMapping("/deleteGitHubRepo")
    public RedirectView deleteGitHubRepo(@RequestParam String repoName) throws IOException, GitAPIException {
        gitHubRestApiService.deleteRepository(repoName);
        return new RedirectView("/githubRepositories");
    }

    @GetMapping("/githubBranchesByRepo")
    public String githubForm(Model model, @RequestParam String repoName) {
        model.addAttribute("path", "path");
        model.addAttribute("repoName", repoName);
        model.addAttribute("AllBranch", gitHubRestApiService.getAllBranchesByRepo(repoName));
        return "githubBranches";
    }

    @GetMapping("/githubAllCommits")
    public String githubAllCommits(Model model, @RequestParam String repoName) {
        model.addAttribute("repoName", repoName);
        model.addAttribute("AllCommits", gitHubRestApiService.getCommitsOfRepo(repoName));
        return "githubCommits";
    }

    @GetMapping("/githubAllContributors")
    public String githubAllContributors(Model model, @RequestParam String repoName){
        model.addAttribute("repoName",repoName);
        model.addAttribute("allContributors", gitHubRestApiService.getReposContributions(repoName));
        return "githubContributors";
    }

    @GetMapping("/githubAllActivities")
    public String githubAllActivities(Model model, @RequestParam String repoName){
        model.addAttribute("repoName",repoName);
        model.addAttribute("allActivities", gitHubRestApiService.getReposActivity(repoName));
        return "githubActivities";
    }

    @PostMapping("/githubPrintCommits")
    public String githubPrintCommits(Model model,@RequestParam String repoName, String fileName, boolean timestamp, boolean message, boolean email) throws IOException, GitAPIException {
        model.addAttribute("repoName", repoName);
        model.addAttribute("AllCommits", gitHubRestApiService.githubPrintCommits(repoName, fileName, timestamp, message, email));
        return "githubCommits";
    }
}
