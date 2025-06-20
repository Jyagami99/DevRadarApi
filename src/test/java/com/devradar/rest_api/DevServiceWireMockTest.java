// DevServiceWireMockTest.java
package com.devradar.rest_api;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.gson.Gson;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DevServiceWireMockTest {

    static WireMockExtension wireMockServer;
    private static final Gson gson = new Gson();

    @BeforeAll
    public static void setupWireMock() throws Exception {
        wireMockServer = WireMockExtension.newInstance()
                .options(WireMockConfiguration.wireMockConfig().port(8089))
                .build();

        wireMockServer.beforeAll(null); // Manually start WireMock for JUnit 5-less setup

        WireMock.configureFor("localhost", 8089);

        stubGitHubUser("coordsUser", "Mock User", "Bio simulada");
    }

    private static void stubGitHubUser(String login, String name, String bio) {
        wireMockServer.stubFor(get(urlEqualTo("/users/" + login))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{" +
                                "\"login\":\"" + login + "\"," +
                                "\"name\":\"" + name + "\"," +
                                "\"bio\":\"" + bio + "\"}")));
    }

    @Test
    void testGithubApiMock() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:8089/users/coordsUser", String.class);
        Assertions.assertNotNull(response);
        assertTrue(response.contains("coordsUser"));
    }
}
