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
import java.util.Arrays;

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
        model.addAttribute("parents", localService.getParents(path));
        return "newHome";
    }

    @GetMapping("/createRepositoryForm")
    public String createRepositoryForm(Model model, @RequestParam String path) {
        model.addAttribute("path", path);
        model.addAttribute("parents", localService.getParents(path));
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
        model.addAttribute("parents", localService.getParents(path));
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

    @PostMapping("/printCommits")
    public RedirectView printCommits(@RequestParam String path, String fileName, boolean timestamp, boolean message, boolean email, String[] authors) throws IOException, GitAPIException {
        localService.printCommits(path, fileName, timestamp, message, email, authors);
        return new RedirectView("/local/fileDetails?path=" + path);
    }

    @GetMapping("/status")
    public String status(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("pathStatus", localService.status(path));
        return "Manage/status";
    }

    @GetMapping("/bin")
    public String bin(Model model) {
        return "Bin/index";
    }

    @GetMapping("/bin/add")
    public String binAdd(Model model) {
        return "Bin/add";
    }

    @PostMapping("/bin/create")
    public String binCreate(@RequestParam String username, @RequestParam String password, @RequestParam String site, @RequestParam String notes) {
        localService.binCreate(username, password, site, notes);
        return "Bin/add";
    }
}
