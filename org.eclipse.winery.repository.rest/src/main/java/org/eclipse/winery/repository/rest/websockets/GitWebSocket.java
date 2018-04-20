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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.rest.datatypes.GitData;
import org.eclipse.winery.repository.rest.resources.apiData.QNameWithTypeApiData;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/git")
public class GitWebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitWebSocket.class);
    private static final Set<GitWebSocket> connections = new CopyOnWriteArraySet<>();
    private Session session;

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        LOGGER.debug(session.getId() + " has opened a connection");
        if (Stream.of(System.getenv("PATH").split(File.pathSeparator))
            .map(Paths::get)
            .anyMatch(path -> Files.exists(path.resolve("git-lfs.exe")) || Files.exists(path.resolve("git-lfs")))) {
            writeInSession(session, "{ \"lfsAvailable\": true }");
        }
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.debug("Session " + session.getId() + " has ended");
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        LOGGER.trace("", t);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GitData data = mapper.readValue(message, GitData.class);
            if (RepositoryFactory.getRepository() instanceof GitBasedRepository) {
                if (data.reset) {
                    try {
                        ((GitBasedRepository) RepositoryFactory.getRepository()).cleanAndResetHard();
                        writeInSession(session, "{ \"success\": \"Successfully reset the workingtree!\" }");
                        ((GitBasedRepository) RepositoryFactory.getRepository()).postEventMap();
                    } catch (GitAPIException e) {
                        LOGGER.warn("Git reset failed", e);
                        writeInSession(session, "{ \"error\": \"Couldn't parse message!\" }");
                    }
                } else {
                    boolean doCommit = false;

                    if (Objects.nonNull(data.commitMessage)) {
                        doCommit = !data.commitMessage.isEmpty();
                    }

                    synchronized (session) {
                        try {
                            if (doCommit) {
                                GitBasedRepository repository = (GitBasedRepository) RepositoryFactory.getRepository();

                                if (Objects.nonNull(data.itemsToCommit) && data.itemsToCommit.size() > 0) {
                                    List<String> list = new ArrayList<>();

                                    Iterable<String> iterable = Iterables.concat(
                                        repository.getStatus().getAdded(),
                                        repository.getStatus().getUntracked(),
                                        repository.getStatus().getModified());

                                    for (QNameWithTypeApiData name : data.itemsToCommit) {
                                        String pattern = name.type + "/" + Util.URLencode(name.namespace) + "/" + name.localname;
                                        for (String item : iterable) {
                                            if (item.startsWith(pattern)) {
                                                list.add(item);
                                            }
                                        }
                                    }

                                    String[] patterns = new String[list.size()];
                                    list.toArray(patterns);

                                    repository.addCommit(patterns, data.commitMessage);
                                } else {
                                    repository.addCommit(data.commitMessage);
                                }

                                writeInSession(session, "{ \"success\": \"Successfully committed changes!\" }");
                            }

                            if (data.refresh) {
                                ((GitBasedRepository) RepositoryFactory.getRepository()).postEventMap();
                            }
                        } catch (GitAPIException exc) {
                            if (doCommit) {
                                LOGGER.warn("Git commit failed", exc);
                                writeInSession(session, "{ \"error\": \"Commit failed!\" }");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            writeInSession(session, "{ \"error\": \"Couldn't parse message!\" }");
        }
    }

    @Subscribe
    public void setGitDifferences(Map<DiffEntry, String> event) {
        (new Thread(new GitLogRunnable(event, new ArrayList(connections)))).start();
    }

    class GitLogRunnable implements Runnable {
        private Map<DiffEntry, String> entryList;
        private List<GitWebSocket> socketList;
        private String jsonString = "[]";

        public GitLogRunnable(Map<DiffEntry, String> entryList, List<GitWebSocket> socketList) {
            this.entryList = entryList;
            this.socketList = socketList;
        }

        public void run() {
            JsonFactory jsonFactory = new JsonFactory();
            StringWriter sw = new StringWriter();
            try {
                JsonGenerator jg = jsonFactory.createGenerator(sw);
                jg.writeStartObject();
                jg.writeFieldName("changes");
                jg.writeStartArray();
                for (DiffEntry entry : this.entryList.keySet()) {
                    jg.writeStartObject();
                    String namePath = entry.getOldPath();
                    if (entry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
                        namePath = entry.getNewPath();
                    }
                    jg.writeStringField("name", namePath);
                    jg.writeStringField("type", entry.getChangeType().name());
                    jg.writeStringField("path", namePath);
                    jg.writeStringField("diffs", entryList.get(entry));
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
                    if (client.session.isOpen()) {
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
