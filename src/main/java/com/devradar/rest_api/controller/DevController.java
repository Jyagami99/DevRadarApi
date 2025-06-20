package com.devradar.rest_api.controller;

import com.devradar.rest_api.model.Dev;
import com.devradar.rest_api.model.DevRequest;
import com.devradar.rest_api.service.DevService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devs")
@CrossOrigin(origins = "*")
public class DevController {
    private final DevService devService;

    public DevController(DevService devService) {
        this.devService = devService;
    }

    @GetMapping
    public List<Dev> getAllDevs() {
        return devService.getAllDevs();
    }

    @GetMapping("/id/{id}")
    public Dev getDevById(@PathVariable Long id) {
        return devService.getDevById(id);
    }

    @GetMapping("/username/{username}")
    public Dev getDevByUsername(@PathVariable String username) {
        return devService.getDevByUsername(username);
    }

    @GetMapping("/search/tech")
    public List<Dev> searchByTech(@RequestParam String tech) {
        return devService.searchByTech(tech);
    }

    @GetMapping("/search/location")
    public List<Dev> searchByLocation(@RequestParam Double lat, @RequestParam Double lon, @RequestParam Double distance) {
        return devService.searchByLocation(lat, lon, distance);
    }

    @PostMapping
    public Dev createDev(@RequestBody DevRequest devRequest) {
        return devService.createDev(devRequest.githubUsername(), devRequest.techs(), devRequest.latitude(), devRequest.longitude());
    }

    @PutMapping("/{id}")
    public Dev updateDev(@PathVariable Long id, @RequestBody DevRequest devRequest) {
        return devService.updateDev(
                id,
                devRequest.githubUsername(),
                devRequest.techs(),
                devRequest.latitude(),
                devRequest.longitude()
        );
    }

}