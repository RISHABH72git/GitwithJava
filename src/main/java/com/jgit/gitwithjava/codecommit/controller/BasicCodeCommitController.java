package com.jgit.gitwithjava.codecommit.controller;

import com.jgit.gitwithjava.codecommit.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import software.amazon.awssdk.regions.Region;

import java.util.List;

@Controller
@RequestMapping("codecommit")
public class BasicCodeCommitController {

    RestService restService;

    @Autowired
    public BasicCodeCommitController(RestService restService) {
        this.restService = restService;
    }

    @GetMapping("repositories")
    public String repositories(Model model, String region) {
        model.addAttribute("repositories", restService.repositories(Region.of(region)));
        return "CodeCommit/index.html";
    }
}
