/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.tools.deployablecomponents.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.winery.tools.deployablecomponents.DeployableComponents;
import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubCrawler implements ICrawlerImplementation {

    public final static Logger LOGGER = LoggerFactory.getLogger(GithubCrawler.class);

    private int lastId = 0;
    private URL baseUrl;
    private String authEncoded;
    private boolean authenticated;
    private int rateLimitRemaining = 5000;
    private Instant rateLimitReset;
    private int rateLimitRemainingSearch = 30;
    private Instant rateLimitResetSearch;

    GithubCrawler(String githubName, String oauthToken) {
        try {
            baseUrl = new URL("https://api.github.com/");
            String auth = githubName + ":" + oauthToken;
            authenticated = !oauthToken.isEmpty();
            authEncoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        } catch (MalformedURLException e) {
            LOGGER.error("Wrong format of crawler URL", e);
        }
    }

    public void setStartPoint(int startId) {
        lastId = startId;
    }

    public List<Dockerfile> crawlDockerfiles() throws IOException {
        Repository newRepository = getNextRepository();
        return searchRepositoryForDockerfiles(newRepository);
    }

    private Repository getNextRepository() throws IOException {
        URL url = new URL(baseUrl + "repositories?since=" + lastId);
        String responseString = HttpGet(url, true, false);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseArray = mapper.readTree(responseString);
        JsonNode response = responseArray.get(0);
        lastId = response.get("id").asInt();

        return parseJsonToRepository(response);
    }

    private Repository parseJsonToRepository(JsonNode json) throws MalformedURLException {
        String name = json.get("full_name").asText();
        String urlString = json.get("url").asText();

        URL url = new URL(urlString);

        return new Repository(name, url);
    }

    private String HttpGet(URL url, boolean isInRateLimit, boolean isSearch) throws IOException {
        if (isInRateLimit && isSearch && rateLimitRemainingSearch <= 0) {
            sleepSearchRateLimit();
        }
        if (isInRateLimit && rateLimitRemaining <= 0) {
            sleepRateLimit();
        }

        boolean successful;
        StringBuilder responseBuilder;
        HttpURLConnection connection;
        int fails = 0;

        // as long as rate limit is not exceeded this do-while is done only once
        do {
            if (fails >= DeployableComponents.MAX_FAILED_CRAWLER_REQUESTS) {
                throw new IOException("HTTP request failed " + fails + " times. Request canceled!");
            }
            responseBuilder = new StringBuilder();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            if (authenticated) {
                connection.setRequestProperty("Authorization", "Basic " + authEncoded);
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                // 403 Forbidden might be rate limit
                if (connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                    String limitRemaining = connection.getHeaderField("X-RateLimit-Remaining");
                    if (limitRemaining != null && Integer.parseInt(limitRemaining) == 0) {
                        successful = false;
                        if (isSearch) {
                            rateLimitResetSearch = Instant.ofEpochSecond(Long.parseLong(connection.getHeaderField("X-RateLimit-Reset")));
                            connection.disconnect();
                            sleepSearchRateLimit();
                        } else {
                            rateLimitReset = Instant.ofEpochSecond(Long.parseLong(connection.getHeaderField("X-RateLimit-Reset")));
                            connection.disconnect();
                            sleepRateLimit();
                        }
                    } else {
                        fails++;
                        successful = false;
                        LOGGER.error("An error occurred in a http get request: " + connection.getResponseCode() + " - " + connection.getResponseMessage() + ". Failed " + fails + ". time.");
                        connection.disconnect();
                        sleepOneMinute();
                    }
                } else {
                    fails++;
                    successful = false;
                    LOGGER.error("An error occurred in a http get request: " + connection.getResponseCode() + " - " + connection.getResponseMessage() + ". Failed " + fails + ". time.");
                    connection.disconnect();
                    sleepOneMinute();
                }
            } else {
                successful = true;
            }
        } while (!successful);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            if (isInRateLimit && isSearch) {
                rateLimitRemainingSearch = Integer.parseInt(connection.getHeaderField("X-RateLimit-Remaining"));
                rateLimitResetSearch = Instant.ofEpochSecond(Long.parseLong(connection.getHeaderField("X-RateLimit-Reset")));
                LOGGER.info("Search rate limit: " + rateLimitRemainingSearch);
            } else if (isInRateLimit) {
                rateLimitRemaining = Integer.parseInt(connection.getHeaderField("X-RateLimit-Remaining"));
                rateLimitReset = Instant.ofEpochSecond(Long.parseLong(connection.getHeaderField("X-RateLimit-Reset")));
                LOGGER.info("Rate limit: " + rateLimitRemaining);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            connection.disconnect();
        }

        return responseBuilder.toString();
    }

    private void sleepSearchRateLimit() {
        long sleepTime = rateLimitResetSearch.toEpochMilli() - Instant.now().toEpochMilli() + 5000;
        if (sleepTime > 0) {
            LOGGER.info("Rate Limit: GitHub search api rate limit exceeded! Thread sleeps " + sleepTime + " milliseconds.");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to wait for rate limit", e);
            }
        }
    }

    private void sleepRateLimit() {
        long sleepTime = rateLimitReset.toEpochMilli() - Instant.now().toEpochMilli() + 5000;
        if (sleepTime > 0) {
            LOGGER.info("Rate Limit: GitHub api rate limit exceeded! Thread sleeps " + sleepTime + " milliseconds.");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to wait for rate limit", e);
            }
        }
    }

    private void sleepOneMinute() {
        long sleepTime = 60000;
        LOGGER.info("Thread sleeps " + sleepTime + " milliseconds.");
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            LOGGER.error("Failed to wait for rate limit", e);
        }
    }

    private List<Dockerfile> searchRepositoryForDockerfiles(Repository repository) throws IOException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("search/code?q=FROM+filename:Dockerfile+repo:");
        urlBuilder.append(repository.getName());
        URL url = new URL(urlBuilder.toString());
        String response = HttpGet(url, true, true);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(response);
        LOGGER.info("Searched in repo " + repository.getName());
        return extractDockerfiles(responseJson, repository);
    }

    private List<Dockerfile> extractDockerfiles(JsonNode response, Repository repository) throws IOException {
        int numberOfFoundDockerfiles = response.get("total_count").asInt();
        JsonNode responseDockerfiles = response.get("items");
        List<Dockerfile> dockerfiles = new ArrayList<>();

        for (int i = 0; i < numberOfFoundDockerfiles; i++) {
            JsonNode dockerfileJson = responseDockerfiles.get(i);
            String gitPathDockerfile = dockerfileJson.get("path").asText();

            URL extendedInformationUrl = new URL(dockerfileJson.get("url").asText());
            String extendedInformationResponse = HttpGet(extendedInformationUrl, true, false);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode extendedInformationResponseJson = mapper.readTree(extendedInformationResponse);
            URL downloadUrlDockerfile = new URL(extendedInformationResponseJson.get("download_url").asText());
            String dockerfileContent = HttpGet(downloadUrlDockerfile, false, false);

            Dockerfile dockerfile = new Dockerfile(gitPathDockerfile, dockerfileContent, repository.getName());
            dockerfiles.add(dockerfile);

            LOGGER.info("Downloaded new dockerfile: " + gitPathDockerfile);
        }

        return dockerfiles;
    }
}
