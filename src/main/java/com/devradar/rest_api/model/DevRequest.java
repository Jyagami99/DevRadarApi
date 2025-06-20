package com.devradar.rest_api.model;

import java.util.List;

public record DevRequest(String githubUsername, List<String> techs, Double latitude, Double longitude) {
}