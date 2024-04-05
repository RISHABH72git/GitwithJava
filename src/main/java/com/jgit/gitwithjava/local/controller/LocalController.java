package com.jgit.gitwithjava.local.controller;

import com.jgit.gitwithjava.custom.model.GitClone;
import com.jgit.gitwithjava.local.service.LocalService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Controller
@RequestMapping("local")
public class LocalController {

    LocalService localService;

    @Autowired
    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    @GetMapping("")
    public String localAllFile(Model model, String path) {
        model.addAttribute("path", path);
        model.addAttribute("getAllHome", localService.localAllFile(path));
        return "newHome";
    }

    @GetMapping("/createRepositoryForm")
    public String createRepositoryForm(Model model, @RequestParam String path) {
        model.addAttribute("path", path);
        return "createRepositoryForm";
    }

    @PostMapping("/createRepository")
    public RedirectView createRepository(Model model, @RequestParam String path, @RequestParam String repo) throws GitAPIException, IOException {
        String repoPath = "";
        String customPath = "";
        if (!path.equals("")) {
            customPath = "?path=" + path;
            repoPath = path + "/" + repo;
        } else {
            repoPath = repo;
        }
        localService.createRepository(repoPath);
        return new RedirectView("/local" + customPath);
    }

    @GetMapping("/cloneRepositoryForm")
    public String cloneRepositoryForm(Model model, @RequestParam String path) {
        model.addAttribute("path", path);
        model.addAttribute("gitClone", new GitClone());
        return "cloneRepositoryForm";
    }

    @PostMapping("/cloneRepository")
    public RedirectView cloneRepository(Model model, String path, GitClone gitClone) throws GitAPIException {
        localService.cloneRepository(gitClone, path);
        String customPath = "";
        if (!path.equals("")) {
            customPath = "?path=" + path;
        }
        return new RedirectView("/local" + customPath);
    }

    @GetMapping("/fileDetails")
    public String fileDetails(Model model, String path) {
        model.addAttribute("path", path);
        return "details";
    }

    @PostMapping("/createFolder")
    public RedirectView createFolder(String path, String name) {
        localService.createFolder(path, name);
        String customPath = "";
        if (!path.equals("")) {
            customPath = "?path=" + path;
        }
        return new RedirectView("/local" + customPath);
    }

    @PostMapping("/createFile")
    public RedirectView createFile(String path, String filename) throws IOException {
        localService.createFile(path, filename);
        String customPath = "";
        if (!path.equals("")) {
            customPath = "?path=" + path;
        }
        return new RedirectView("/local" + customPath);
    }
}
