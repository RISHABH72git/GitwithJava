package com.jgit.gitwithjava.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.github.model.Users;
import com.jgit.gitwithjava.github.model.branch.Branch;
import com.jgit.gitwithjava.github.model.branches.Branches;
import com.jgit.gitwithjava.github.model.CreateRepository;
import com.jgit.gitwithjava.github.model.ListRepository;
import com.jgit.gitwithjava.github.model.RepoData;
import com.jgit.gitwithjava.github.model.commits.GetCommit;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@Log4j2 @Service
public class GitHubRestApiService {

    private HttpHeaders getHttpHeadersWithToken() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(DefaultCredentials.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public Users getUsers(){
        final String GET_USERS = "https://api.github.com/users/"+DefaultCredentials.getGitUsername();
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Users> response = restTemplate.exchange(GET_USERS, HttpMethod.GET, entity, Users.class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public CreateRepository createRepository(RepoData repoData) throws JsonProcessingException {
        final String CREATE_REPOSITORY = "https://api.github.com/user/repos";
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(repoData);
        HttpEntity<String> entity = new HttpEntity<>(json, getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CreateRepository> response = restTemplate.postForEntity(URI.create(CREATE_REPOSITORY), entity, CreateRepository.class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public ListRepository[] listAllRepository() throws Exception {
        final String LIST_REPOSITORY = "https://api.github.com/user/repos?visibility=all";
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ListRepository[]> response = restTemplate.exchange(LIST_REPOSITORY, HttpMethod.GET, entity, ListRepository[].class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteRepository(String repos) {
        final String DELETE_REPOSITORY = "https://api.github.com/repos/" + DefaultCredentials.getGitUsername() + "/" + repos;
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(URI.create(DELETE_REPOSITORY), HttpMethod.DELETE, entity, String.class);
            System.out.println(response.getStatusCode());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public CreateRepository getRepository(String repos) {
        final String GET_REPOSITORY = "https://api.github.com/repos/" + DefaultCredentials.getGitUsername() + "/" + repos;
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CreateRepository> response = restTemplate.exchange(URI.create(GET_REPOSITORY), HttpMethod.GET, entity, CreateRepository.class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public GetCommit[] getCommitsOfRepo(String repos){
        final String GET_COMMITS = "https://api.github.com/repos/" + DefaultCredentials.getGitUsername() + "/" + repos+"/commits";
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GetCommit[]> response = restTemplate.exchange(URI.create(GET_COMMITS), HttpMethod.GET, entity, GetCommit[].class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteAllRepository() throws Exception {
        ListRepository [] allRepository = listAllRepository();
        for (ListRepository listRepository : allRepository){
            deleteRepository(listRepository.getName());
            log.warn("{} is Deleted from Github",listRepository.getName());
        }
    }

    public Branches[] getAllBranchesByRepo(String repoName){
        String GET_LIST_BRANCH = "https://api.github.com/repos/"+DefaultCredentials.getGitUsername()+"/"+repoName+"/branches";
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Branches[]> response = restTemplate.exchange(URI.create(GET_LIST_BRANCH), HttpMethod.GET, entity, Branches[].class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public Branch getBranchByName(String repoName, String branchName){
        String GET_BRANCH = "https://api.github.com/repos/"+DefaultCredentials.getGitUsername()+"/"+repoName+"/branches/"+branchName;
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Branch> response = restTemplate.exchange(URI.create(GET_BRANCH), HttpMethod.GET, entity, Branch.class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String renameBranchByRepoName(String repoName, String oldBranchName, String newBranchName){
        String GET_BRANCH = "https://api.github.com/repos/"+DefaultCredentials.getGitUsername()+"/"+repoName+"/branches/"+oldBranchName+"/rename";
        String jsonPayload = "{\"new_name\": " + newBranchName + "}";
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload,getHttpHeadersWithToken());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(URI.create(GET_BRANCH), HttpMethod.GET, entity, String.class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
