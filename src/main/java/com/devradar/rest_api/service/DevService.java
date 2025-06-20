package com.devradar.rest_api.service;

import com.devradar.rest_api.model.Dev;
import com.devradar.rest_api.repository.DevRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DevService {
    private final DevRepository devRepository;
    private final Gson gson = new Gson();
    private final RestTemplate restTemplate;

    public DevService(DevRepository devRepository, RestTemplate restTemplate) {
        this.devRepository = devRepository;
        this.restTemplate = restTemplate;
    }

    public List<Dev> getAllDevs() {
        return devRepository.findAll();
    }

    public Dev getDevById(Long id) {
        return devRepository.findById(id)
                .orElseThrow(() -> new DevNotFoundException("Não encontrado dev com o ID: " + id));
    }

    public Dev getDevByUsername(String username) {
        return devRepository.findByGithubUsername(username);
    }

    public List<Dev> searchByTech(String tech) {
        return devRepository.findByTech(tech);
    }

    public List<Dev> searchByLocation(Double lat, Double lon, Double distance) {
        return devRepository.findByLocation(lat, lon, distance);
    }

    public Dev createDev(String githubUsername, List<String> techs, Double latitude, Double longitude) {
        if (githubUsername == null || githubUsername.isBlank()) {
            throw new IllegalArgumentException("GitHub username não pode ser vazio.");
        }
        if (techs == null || techs.isEmpty()) {
            throw new IllegalArgumentException("A lista de tecnologias não pode ser vazia.");
        }
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude e longitude não podem ser nulas.");
        }

        if (devRepository.existsByGithubUsername(githubUsername)) {
            return devRepository.findByGithubUsername(githubUsername);
        }

        JsonObject json = fetchGithubUser(githubUsername);

        Dev dev = new Dev();
        dev.setGithubUsername(githubUsername);
        populateDevWithGithubData(dev, json, githubUsername);
        dev.setTechs(techs);
        dev.setLatitude(latitude);
        dev.setLongitude(longitude);

        return devRepository.save(dev);
    }

    public Dev updateDev(Long id, String githubUsername, List<String> techs, Double latitude, Double longitude) {
        Dev dev = devRepository.findById(id)
                .orElseThrow(() -> new DevNotFoundException("Dev não encontrado com o ID: " + id));

        dev.setTechs(techs);
        dev.setLatitude(latitude);
        dev.setLongitude(longitude);

        JsonObject json = fetchGithubUser(githubUsername);
        populateDevWithGithubData(dev, json, githubUsername);

        return devRepository.save(dev);
    }

    private JsonObject fetchGithubUser(String githubUsername) {
        String url = "https://api.github.com/users/" + githubUsername;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return gson.fromJson(response.getBody(), JsonObject.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Erro ao buscar dados do GitHub para o usuário: " + githubUsername, e);
        }
    }

    private void populateDevWithGithubData(Dev dev, JsonObject json, String githubUsername) {
        dev.setName(json.has("name") && !json.get("name").isJsonNull() ? json.get("name").getAsString() : githubUsername);
        dev.setAvatarUrl(json.get("avatar_url").getAsString());
        dev.setBio(json.has("bio") && !json.get("bio").isJsonNull() ? json.get("bio").getAsString() : null);
    }

    public static class DevNotFoundException extends RuntimeException {
        public DevNotFoundException(String message) {
            super(message);
        }
    }
}
