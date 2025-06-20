package com.devradar.rest_api.repository;

import com.devradar.rest_api.model.Dev;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface  DevRepository extends JpaRepository<Dev, Long> {
    boolean existsByGithubUsername(String githubUsername);
    Dev findByGithubUsername(String githubUsername);

    @Query("SELECT d FROM Dev d WHERE :tech MEMBER OF d.techs")
    List<Dev> findByTech(@Param("tech") String tech);

    @Query("SELECT d FROM Dev d WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(d.latitude)) * cos(radians(d.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(d.latitude)))) < :distance")
    List<Dev> findByLocation(@Param("lat") Double lat, @Param("lon") Double lon, @Param("distance") Double distance);
}
