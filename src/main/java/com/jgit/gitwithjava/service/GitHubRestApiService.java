package com.jgit.gitwithjava.service;

import com.jgit.gitwithjava.githubProjection.CreateRepository;
import com.jgit.gitwithjava.githubProjection.ListRepository;
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

    public CreateRepository createRepository(String repoName, String description, String isPrivate) {
        final String CREATE_REPOSITORY = "https://api.github.com/user/repos";
        String jsonPayload = "{\"name\": " + repoName + ", \"description\": " + description + ",\"private\": " + isPrivate + "}";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, httpHeaders);
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
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ListRepository[]> response = restTemplate.exchange(LIST_REPOSITORY, HttpMethod.GET, entity, ListRepository[].class);
            return Objects.requireNonNull(response.getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
