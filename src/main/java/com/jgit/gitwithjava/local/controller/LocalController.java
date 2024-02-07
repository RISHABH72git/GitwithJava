package com.jgit.gitwithjava.local.controller;

import com.jgit.gitwithjava.local.service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
