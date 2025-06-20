package com.devradar.rest_api;

import com.devradar.rest_api.model.Dev;
import com.devradar.rest_api.repository.DevRepository;
import com.devradar.rest_api.service.DevService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")

class RestApiApplicationTests {

	@Mock
	private DevRepository devRepository;

	@InjectMocks
	private DevService devService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSearchByTech() {
		Dev dev = new Dev();
		dev.setGithubUsername("testuser");
		dev.setTechs(Arrays.asList("Java", "Spring"));
		when(devRepository.findByTech("Java")).thenReturn(List.of(dev));

		List<Dev> result = devService.searchByTech("Java");

		assertEquals(1, result.size());
		assertEquals("testuser", result.getFirst().getGithubUsername());
	}

	@Test
	void testSearchByLocation() {
		Dev dev = new Dev();
		dev.setGithubUsername("geoUser");
		dev.setLatitude(-23.55);
		dev.setLongitude(-46.63);
		when(devRepository.findByLocation(-23.55, -46.63, 10.0)).thenReturn(List.of(dev));

		List<Dev> result = devService.searchByLocation(-23.55, -46.63, 10.0);

		assertEquals(1, result.size());
		assertEquals("geoUser", result.getFirst().getGithubUsername());
	}

	@Test
	void testGetAllDevs() {
		when(devRepository.findAll()).thenReturn(List.of(new Dev(), new Dev()));
		List<Dev> result = devService.getAllDevs();
		assertEquals(2, result.size());
	}

	@Test
	void testCreateDev_NewDev() {
		String githubUsername = "newuser";
		List<String> techs = List.of("Java", "Spring");
		Double lat = -23.55;
		Double lon = -46.63;

		when(devRepository.existsByGithubUsername(githubUsername)).thenReturn(false);
		Dev savedDev = new Dev();
		savedDev.setGithubUsername(githubUsername);
		when(devRepository.save(any(Dev.class))).thenReturn(savedDev);

		Dev result = devService.createDev(githubUsername, techs, lat, lon);
		assertEquals(githubUsername, result.getGithubUsername());
	}

	@Test
	void testCreateDev_ExistingDev() {
		String githubUsername = "existinguser";
		Dev existingDev = new Dev();
		existingDev.setGithubUsername(githubUsername);

		when(devRepository.existsByGithubUsername(githubUsername)).thenReturn(true);
		when(devRepository.findByGithubUsername(githubUsername)).thenReturn(existingDev);

		Dev result = devService.createDev(githubUsername, List.of(), 0.0, 0.0);
		assertEquals(githubUsername, result.getGithubUsername());
	}

	@Test
	void testFindByTech_ReturnsEmptyList() {
		when(devRepository.findByTech("Python")).thenReturn(List.of());
		List<Dev> result = devService.searchByTech("Python");
		assertTrue(result.isEmpty());
	}

	@Test
	void testFindByLocation_ReturnsEmptyList() {
		when(devRepository.findByLocation(0.0, 0.0, 1.0)).thenReturn(List.of());
		List<Dev> result = devService.searchByLocation(0.0, 0.0, 1.0);
		assertTrue(result.isEmpty());
	}

	@Test
	void testCreateDev_SetsFallbackNameAndBio() {
		String githubUsername = "nullbio";
		List<String> techs = List.of("Go");
		Double lat = 10.0;
		Double lon = 10.0;

		when(devRepository.existsByGithubUsername(githubUsername)).thenReturn(false);
		when(devRepository.save(any(Dev.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Dev dev = devService.createDev(githubUsername, techs, lat, lon);

		assertNotNull(dev);
		assertEquals(githubUsername, dev.getGithubUsername());
		assertEquals(techs, dev.getTechs());
	}

	@Test
	void testCreateDev_SetsCoordinates() {
		String githubUsername = "coordsUser";
		List<String> techs = List.of("Node.js");
		Double lat = -12.0;
		Double lon = 45.0;

		when(devRepository.existsByGithubUsername(githubUsername)).thenReturn(false);
		when(devRepository.save(any(Dev.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Dev dev = devService.createDev(githubUsername, techs, lat, lon);

		assertEquals(lat, dev.getLatitude());
		assertEquals(lon, dev.getLongitude());
	}

	@Test
	void testCreateDev_AssignsTechsProperly() {
		String githubUsername = "techuser";
		List<String> techs = List.of("Python", "Django");
		when(devRepository.existsByGithubUsername(githubUsername)).thenReturn(false);
		when(devRepository.save(any(Dev.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Dev dev = devService.createDev(githubUsername, techs, -10.0, -10.0);

		assertEquals(techs, dev.getTechs());
	}
}
