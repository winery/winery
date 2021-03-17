/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.repository.rest.websockets;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.filebased.MultiRepository;
import org.eclipse.winery.repository.rest.datatypes.GitData;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/git")
public class GitWebSocket extends AbstractWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitWebSocket.class);
    private static final Set<GitWebSocket> connections = new CopyOnWriteArraySet<>();

    protected void onOpen() {
        connections.add(this);
        if (Stream.of(System.getenv("PATH").split(File.pathSeparator))
            .map(Paths::get)
            .anyMatch(path -> Files.exists(path.resolve("git-lfs.exe")) || Files.exists(path.resolve("git-lfs")))) {
            writeInSession(session, "{ \"lfsAvailable\": true }");
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            GitData data = JacksonProvider.mapper.readValue(message, GitData.class);
            Map<String, Map<DiffEntry, String>> repoEntryList = new HashMap<>();

            if (RepositoryFactory.getRepository() instanceof MultiRepository) {
                IRepository gitRepo;

                for (IRepository repo : ((MultiRepository) RepositoryFactory.getRepository()).getRepositories()) {
                    gitRepo = repo;

                    if (gitRepo instanceof GitBasedRepository) {
                        handleGitRepository((GitBasedRepository) gitRepo, data, repoEntryList);
                    }
                }
                
                setGitDifferences(repoEntryList);
            } else if (RepositoryFactory.getRepository() instanceof GitBasedRepository) {
                handleGitRepository((GitBasedRepository) RepositoryFactory.getRepository(), data, repoEntryList);
                setGitDifferences(repoEntryList);
            }
        } catch (IOException e) {
            writeInSession(session, "{ \"error\": \"Could not parse message!\" }");
        }
    }

    public void handleGitRepository(GitBasedRepository gitRepo, GitData data, Map<String, Map<DiffEntry, String>> repoEntryList) {
        synchronized (session) {
            String gitUrl;
            if (gitRepo.getRepositoryUrl() != null) {
                gitUrl = gitRepo.getRepositoryUrl();
            } else {
                gitUrl = "Local Repository";
            }

            if (data.reset) {
                try {
                    if (!data.repository.equals("") && data.repository.equals(gitUrl)) {
                        gitRepo.cleanAndResetHard();
                        writeInSession(session, "{ \"success\": \"Successfully reset the workingtree of repository " + gitUrl + "\" }");
                    } else if (data.repository.equals("")) {
                        gitRepo.cleanAndResetHard();
                        writeInSession(session, "{ \"success\": \"Successfully reset the workingtree of all repositories!\" }");
                    }
                } catch (GitAPIException e) {
                    LOGGER.warn("Git reset failed", e);
                    writeInSession(session, "{ \"error\": \"Could not reset repository " + gitUrl + "\" }");
                }
            } else if (data.pull) {
                try {
                    if (!data.repository.equals("") && data.repository.equals(gitUrl)) {
                        gitRepo.pull();
                        writeInSession(session, "{ \"success\": \"Successfully pulled repository " + gitUrl + "\" }");
                    } else if (data.repository.equals("")) {
                        gitRepo.pull();
                        writeInSession(session, "{ \"success\": \"Successfully pulled all repositories!\" }");
                    }
                } catch (GitAPIException e) {
                    LOGGER.warn("Couldn't pull repository", e);
                    writeInSession(session, "{ \"error\": \"Could not pull repository " + gitUrl + "\" }");
                }
            } else if (data.push) {
                try {
                    if (!data.repository.equals("") && data.repository.equals(gitUrl)) {
                        gitRepo.push();
                        writeInSession(session, "{ \"success\": \"Successfully pushed repository " + gitUrl + "\" }");
                    } else if (data.repository.equals("")) {
                        gitRepo.push();
                        writeInSession(session, "{ \"success\": \"Successfully pushed all repositories!\" }");
                    }
                } catch (GitAPIException e) {
                    LOGGER.warn("Couldn't push repository", e);
                    writeInSession(session, "{ \"error\": \"Could not push repository " + gitUrl + "\" }");
                }
            } else if (data.branches) {
                try {
                    if (!data.repository.equals("") && data.repository.equals(gitUrl)) {
                        List<Ref> branchList = gitRepo.listBranches();

                        JsonFactory jsonFactory = new JsonFactory();
                        StringWriter sw = new StringWriter();

                        JsonGenerator jg = jsonFactory.createGenerator(sw);
                        jg.writeStartObject();
                        jg.writeFieldName("branches");
                        jg.writeStartArray();

                        for (Ref branch : branchList) {
                            jg.writeString(branch.getName());
                        }

                        jg.writeEndArray();
                        jg.writeEndObject();
                        jg.close();

                        writeInSession(session, sw.toString());
                    }
                } catch (IOException e) {
                    LOGGER.warn("Could not get branches", e);
                    writeInSession(session, "{ \"error\": \"Could not get branches of repository " + gitUrl + "\" }");
                } catch (GitAPIException e) {
                    LOGGER.warn("Could not get branches", e);
                    writeInSession(session, "{ \"error\": \"Could not get branches of repository " + gitUrl + "\" }");
                }
            } else if (Objects.nonNull(data.checkout)) {
                try {
                    if (!data.repository.equals("") && data.repository.equals(gitUrl)) {
                        gitRepo.checkout(data.checkout);
                        writeInSession(session, "{ \"success\": \"Successfully checked out " + data.checkout + " in repository " + gitUrl + "\" }");
                    }
                } catch (GitAPIException e) {
                    LOGGER.warn("Could not check out", e);
                    writeInSession(session, "{ \"error\": \"Could not check out repository " + gitUrl + "\" }");
                }
            } else if (data.commit) {

                try {

                    if (!data.repository.equals("") && data.repository.equals(gitUrl)) {

                        if (Objects.nonNull(data.itemsToCommit) && data.itemsToCommit.size() > 0) {

                            List<String> list = new ArrayList<>();

                            Iterable<String> iterable = Iterables.concat(
                                gitRepo.getStatus().getAdded(),
                                gitRepo.getStatus().getUntracked(),
                                gitRepo.getStatus().getModified());

                            for (String name : data.itemsToCommit) {
                                for (String item : iterable) {
                                    if (item.contains(name)) {
                                        list.add(item);
                                    }
                                }
                            }

                            String[] patterns = new String[list.size()];
                            list.toArray(patterns);

                            gitRepo.addCommit(patterns, data.commitMessage);
                        } else {
                            gitRepo.addCommit(data.commitMessage);
                        }
                        writeInSession(session, "{ \"success\": \"Successfully committed changes to repository " + gitUrl + "\" }");
                    }
                } catch (GitAPIException exc) {
                    LOGGER.warn("Git commit failed", exc);
                    writeInSession(session, "{ \"error\": \"Commit failed!\" }");
                }
            }

            try {
                repoEntryList.put(gitUrl, gitRepo.postEventMap());
            } catch (GitAPIException e) {
                LOGGER.warn("Couldn't refresh repository", e);
                writeInSession(session, "{ \"error\": \"Could not refresh repository!\" }");
            }
        }
    }

    @Subscribe
    public void setGitDifferences(Map<String, Map<DiffEntry, String>> event) {
        (new Thread(new GitLogRunnable(event, new ArrayList(connections)))).start();
    }

    class GitLogRunnable implements Runnable {
        private Map<String, Map<DiffEntry, String>> repoList;
        private List<GitWebSocket> socketList;
        private String jsonString = "[]";

        public GitLogRunnable(Map<String, Map<DiffEntry, String>> repoList, List<GitWebSocket> socketList) {
            this.repoList = repoList;
            this.socketList = socketList;
        }

        public void run() {
            JsonFactory jsonFactory = new JsonFactory();
            StringWriter sw = new StringWriter();

            try {
                JsonGenerator jg = jsonFactory.createGenerator(sw);
                jg.writeStartObject();
                jg.writeFieldName("repos");
                jg.writeStartArray();

                for (String repositoryName : this.repoList.keySet()) {
                    Map<DiffEntry, String> repo = this.repoList.get(repositoryName);
                    jg.writeStartObject();
                    jg.writeStringField("name", repositoryName);
                    jg.writeFieldName("changes");
                    jg.writeStartArray();
                    for (DiffEntry entry : repo.keySet()) {
                        jg.writeStartObject();
                        String namePath = entry.getOldPath();
                        if (entry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                            namePath = entry.getNewPath();
                        }
                        jg.writeStringField("name", namePath);
                        jg.writeStringField("type", entry.getChangeType().name());
                        jg.writeStringField("path", namePath);
                        jg.writeStringField("diffs", repo.get(entry));
                        jg.writeEndObject();
                    }
                    jg.writeEndArray();
                    jg.writeEndObject();
                }

                jg.writeEndArray();
                jg.writeEndObject();
                jg.close();
                this.jsonString = sw.toString();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            List<GitWebSocket> closedSessions = new ArrayList<>();
            for (GitWebSocket client : this.socketList) {
                synchronized (client) {
                    if (client.session != null && client.session.isOpen()) {
                        writeInSession(client.session, jsonString);
                    } else {
                        closedSessions.add(client);
                    }
                }
            }
            this.socketList.removeAll(closedSessions);
        }
    }

    private static synchronized void writeInSession(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.trace("Writing to WebSocket failed", e);
        }
    }
}
