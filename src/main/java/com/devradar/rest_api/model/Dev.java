package com.devradar.rest_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Dev {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String githubUsername;
    private String name;
    private String avatarUrl;
    private String bio;

    @ElementCollection
    private List<String> techs;

    private Double latitude;
    private Double longitude;
}
