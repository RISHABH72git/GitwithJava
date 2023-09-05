package com.jgit.gitwithjava.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.jgit.gitwithjava.github.service.GitHubRestApiService;
import org.apache.logging.log4j.util.Strings;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

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

    @PostMapping("/createGitHubRepo")
    public String createGitHubRepo(Model model,Object repoModel) throws Exception {
        //model.addAttribute("listAllRepository",gitHubRestApiService.listAllRepository());
        return "githubRepo";
    }
}
