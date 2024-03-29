package com.jgit.gitwithjava.frontend.controller;

import com.jgit.gitwithjava.frontend.service.CrudBranchService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CrudBranchController {

    CrudBranchService crudBranchService;

    @Autowired
    public CrudBranchController(CrudBranchService crudBranchService) {
        this.crudBranchService = crudBranchService;
    }


    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("gitFileMap", crudBranchService.getGitFileNameWithPath());
        return "home";
    }

    @GetMapping("/commit")
    public String commitPage(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("AllCommits", crudBranchService.getCommits(path));
        return "commit";
    }

    @GetMapping("/branch")
    public String branchPage(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("AllBranch", crudBranchService.gitBranch(path));
        return "branch";
    }

    @GetMapping("/status")
    public String statusPage(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("status", crudBranchService.gitStatus(path));
        return "status";
    }

    @GetMapping("/author")
    public String authorPage(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("author", crudBranchService.getAllAuthorName(path));
        return "author";
    }

    @GetMapping("/config")
    public String configPage(Model model, @RequestParam String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("ObjectList", crudBranchService.getGitConfig(path));
        return "config";
    }

    @GetMapping("/getCommitDetails")
    public String getCommitDetails(Model model, @RequestParam String path, @RequestParam String commitId) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("commitId", commitId);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("commitDetails", crudBranchService.getCommitDetails(path, commitId));
        return "commit";
    }

    @GetMapping("/form")
    public String formPage(Model model) {
        return "form";
    }

    @PostMapping("/createRepo")
    public String createRepoPage(Model model, @RequestParam String repo) throws GitAPIException, IOException {
        Map<String, Object> allFile = crudBranchService.gitInitialize(repo);
        model.addAttribute("repoName", allFile.get("repoName"));
        model.addAttribute("path", allFile.get("path"));
        model.addAttribute("listPath", allFile.get("listPath"));
        return "listFilesAndGitBlame";
    }

    @PostMapping("/createBranch")
    public RedirectView createBranch(@RequestParam String path, String branchName) throws IOException, GitAPIException {
        crudBranchService.createBranch(path, branchName);
        return new RedirectView("/branch?path=" + path);
    }

    @GetMapping("/deleteBranch")
    public RedirectView deleteBranch(@RequestParam String path, @RequestParam String branchName) throws IOException, GitAPIException {
        crudBranchService.deleteBranch(path, branchName);
        return new RedirectView("/branch?path=" + path);
    }

    @GetMapping("/gitDiff")
    public String showDiff(Model model, @RequestParam String path, @RequestParam String firstCommit, @RequestParam String secondCommit) {
        try {
            model.addAttribute("diffContent", crudBranchService.showDiffParentAndChildCommit(path, firstCommit, secondCommit).lines().collect(Collectors.toList()));
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            model.addAttribute("diffContent", "Error reading the diff file.");
        }
        return "gitDiff";
    }

    @PostMapping("/printCommits")
    public RedirectView printCommits(@RequestParam String path, String fileName, boolean timestamp, boolean message, boolean email) throws IOException, GitAPIException {
        crudBranchService.printsAllCommits(path, fileName, timestamp, message, email);
        return new RedirectView("/commit?path=" + path);
    }

    @PostMapping("/printAuthorsCommits")
    public RedirectView printAuthorsCommits(@RequestParam String path, String fileName, boolean timestamp, boolean message, boolean email, String emailAddress) throws IOException, GitAPIException {
        crudBranchService.printAuthorsCommits(path, fileName, timestamp, message, email, emailAddress);
        return new RedirectView("/author?path=" + path);
    }

    @GetMapping("/listFiles")
    public String getAllFiles(Model model, @RequestParam String path) throws IOException {
        model.addAttribute("repoName", path);
        model.addAttribute("path", path);
        model.addAttribute("listPath", crudBranchService.getAllFilesFromDirectory(path));
        return "listFilesAndGitBlame";
    }

    @GetMapping("/blame")
    public String getBlame(Model model, @RequestParam String path, @RequestParam String fileName, boolean print) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("repoName", path.substring(path.lastIndexOf("/") + 1));
        model.addAttribute("fileName", fileName);
        model.addAttribute("blameFile", fileName.substring(fileName.lastIndexOf("/") + 1));
        model.addAttribute("fileWithBlame", crudBranchService.getBlame(path, fileName, print));
        return "listFilesAndGitBlame";
    }

    @GetMapping("/cloneRepository")
    public RedirectView cloneRepository(@RequestParam String cloneUrl, String repoName) throws GitAPIException {
        crudBranchService.cloneRepository(cloneUrl, repoName);
        return new RedirectView("/");
    }

    /*@GetMapping("/newHome")
    public String newHome(Model model, String path) {
        model.addAttribute("path", path);
        model.addAttribute("getAllHome", crudBranchService.getAllHome(path));
        return "newHome";
    }*/

    @GetMapping("/directoryDetails")
    public String directoryDetails(Model model, String path) throws GitAPIException, IOException {
        model.addAttribute("path", path);
        model.addAttribute("pieChart", crudBranchService.getAuthorsNameAndCommitsCount(path));
        model.addAttribute("scatterChart", crudBranchService.getLastDayCommits(path).get("commits"));
        model.addAttribute("scatterChartDate", crudBranchService.getLastDayCommits(path).get("lastDate"));
        model.addAttribute("calenderChart", crudBranchService.getAllCommitInCalenderChart(path));
        return "directoryDetails";
    }

    @PostMapping("/addCommit")
    public RedirectView addCommit(@RequestParam String path, @RequestParam String message, @RequestParam String[] selectedValues) throws GitAPIException, IOException {
        crudBranchService.addCommit(path, message, selectedValues);
        return new RedirectView("/status?path=" + path);
    }

    /*@GetMapping("/directoryDetails")
    public String directoryDetails(Model model, String path){
        model.addAttribute("path", DefaultCredentials.getRootFolder()+path);
        return "details";
    }*/

}