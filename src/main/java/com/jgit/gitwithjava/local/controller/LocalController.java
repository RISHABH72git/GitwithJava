package com.jgit.gitwithjava.local.controller;

import com.jgit.gitwithjava.core.model.GitClone;
import com.jgit.gitwithjava.local.model.ActionType;
import com.jgit.gitwithjava.local.model.SiteDetail;
import com.jgit.gitwithjava.local.model.StatusType;
import com.jgit.gitwithjava.local.service.LocalService;
import jakarta.xml.bind.JAXBException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
    public String status(Model model, @RequestParam String path, @RequestParam StatusType type) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("type", type.getValue());
        model.addAttribute("pathStatus", localService.status(path, type.getValue()));
        return "Manage/status";
    }

    @GetMapping("/bin")
    public String bin(Model model) throws JAXBException {
        model.addAttribute("details", localService.bin());
        return "Bin/index";
    }

    @GetMapping("/bin/add")
    public String binAdd(Model model) {
        return "Bin/add";
    }

    @PostMapping("/bin/create")
    public RedirectView binCreate(@RequestParam String username, @RequestParam String password, @RequestParam String site, @RequestParam String notes, String head) throws JAXBException, FileNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        localService.binCreate(username, password, site, notes, head);
        return new RedirectView("/local/bin");
    }

    @GetMapping("/bin/edit")
    public String binEdit(Model model, @RequestParam String id) throws JAXBException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SiteDetail siteDetail = localService.binEdit(id);
        model.addAttribute("username", siteDetail.getUsername());
        model.addAttribute("password", siteDetail.decryptPassword());
        model.addAttribute("site", siteDetail.getSite());
        model.addAttribute("notes", siteDetail.getNotes());
        model.addAttribute("id", siteDetail.getId());
        model.addAttribute("head", siteDetail.getHead());
        return "Bin/edit";
    }

    @PostMapping("/bin/modify")
    public RedirectView binModify(@RequestParam String username, @RequestParam String password, @RequestParam String site, @RequestParam String notes, @RequestParam String id, @RequestParam String head) throws JAXBException, FileNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        localService.binModify(username, password, site, notes, id, head);
        return new RedirectView("/local/bin");
    }

    @GetMapping("/bin/delete")
    public RedirectView binDelete(@RequestParam String id) throws JAXBException, FileNotFoundException {
        localService.binDelete(id);
        return new RedirectView("/local/bin");
    }

    @PostMapping("/status/action")
    public RedirectView statusAction(@RequestParam List<String> selectedFile, @RequestParam StatusType type, @RequestParam String path, @RequestParam ActionType action, String message) throws IOException, GitAPIException {
        if (!selectedFile.isEmpty()) {
            localService.statusAction(path, selectedFile, type, action, message);
        }
        return new RedirectView("/local/status?path=" + path + "&type=" + type.getValue());
    }

    @GetMapping("/resetToHead")
    public RedirectView resetToHead(@RequestParam String path, @RequestParam String type) throws IOException, GitAPIException {
        localService.resetToHead(path);
        return new RedirectView("/local/status?path=" + path + "&type=" + type);
    }

    @GetMapping("/reset")
    public String reset(Model model, @RequestParam String path) throws IOException, GitAPIException {
        model.addAttribute("path", path);
        model.addAttribute("commits", localService.getCommitsByLimit(path, 10));
        return "Manage/reset";
    }

    @PostMapping("/reset")
    public RedirectView reset(@RequestParam String path, @RequestParam String type, @RequestParam String commitId) throws GitAPIException, IOException {
        localService.reset(path, type, commitId);
        return new RedirectView("/local/reset?path=" + path);
    }

    @GetMapping("/revert")
    public String revert(Model model, @RequestParam String path) throws IOException, GitAPIException {
        model.addAttribute("path", path);
        model.addAttribute("commits", localService.getCommitsByLimit(path, 10));
        return "Manage/revert";
    }

    @PostMapping("/revert")
    public RedirectView revert(@RequestParam String path, @RequestParam String commitId) throws GitAPIException, IOException {
        localService.revert(path, commitId);
        return new RedirectView("/local/revert?path=" + path);
    }

    @GetMapping("/operations")
    public String operations(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("RemoteConfig", localService.remoteConfigList(path));
        return "Manage/operations";
    }

    @GetMapping("/operations/push")
    public RedirectView operationPush(Model model, @RequestParam String path, @RequestParam String name, @RequestParam String uri) throws GitAPIException, IOException {
        System.out.println(name);
        System.out.println(uri);
        return new RedirectView("/local/operations?path=" + path);
    }
}
