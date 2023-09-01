package com.jgit.gitwithjava.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgit.gitwithjava.githubProjection.CreateRepository;
import com.jgit.gitwithjava.githubProjection.ListRepository;
import com.jgit.gitwithjava.githubProjection.RepoData;
import com.jgit.gitwithjava.githubProjection.commits.GetCommit;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Objects;

@Log4j2
public class GitHubRestApiService {

    public String getToken() {
        String key = "";
        try {
            key = Files.readString(new File(".apiToken").toPath());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return key;
    }

    private HttpHeaders getHttpHeadersWithToken() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
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

    public void deleteRepository(String owner, String repos) {
        final String DELETE_REPOSITORY = "https://api.github.com/repos/" + owner + "/" + repos;
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

    public CreateRepository getRepository(String owner, String repos) {
        final String GET_REPOSITORY = "https://api.github.com/repos/" + owner + "/" + repos;
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

    public GetCommit[] getCommitsOfRepo(String owner, String repos){
        final String GET_COMMITS = "https://api.github.com/repos/" + owner + "/" + repos+"/commits";
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
}
