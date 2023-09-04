package com.jgit.gitwithjava.frontend.controller;

import com.jgit.gitwithjava.github.service.GitHubRestApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GithubController {

    @Autowired
    GitHubRestApiService gitHubRestApiService;

    @GetMapping("/githubRepositories")
    public String githubRepositories(Model model) throws Exception {
        model.addAttribute("listAllRepository",gitHubRestApiService.listAllRepository());
        return "githubRepo";
    }

    @GetMapping("/githubForm")
    public String githubForm(){
        return "githubForm";
    }
}
