/**
 * Copyright (c) 2017 University of Stuttgart. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 and the Apache License 2.0 which both accompany this
 * distribution, and are available at http://www.eclipse.org/legal/epl-v20.html and
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
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
			writeInSession(session, "git-lfs");
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
		if (RepositoryFactory.getRepository() instanceof GitBasedRepository) {
			if (message.equals("reset")) {
				try {
					((GitBasedRepository) RepositoryFactory.getRepository()).cleanAndResetHard();
					writeInSession(session, "reset success");
					((GitBasedRepository) RepositoryFactory.getRepository()).postEventMap();
				} catch (GitAPIException e) {
					LOGGER.warn("Git reset failed", e);
					writeInSession(session, "reset failed");
				}
			} else {
				boolean doCommit = !message.isEmpty();
				synchronized (session) {
					try {
						if (doCommit) {
							((GitBasedRepository) RepositoryFactory.getRepository()).addCommit(message);
							writeInSession(session, "commit success");
						} else {
							((GitBasedRepository) RepositoryFactory.getRepository()).postEventMap();
						}
					} catch (GitAPIException exc) {
						if (doCommit) {
							LOGGER.warn("Git commit failed", exc);
							writeInSession(session, "commit failed");
						}
					}
				}
			}
		}
	}

	@Subscribe
	public void setGitDifferences(Map<DiffEntry, String> event) {
		(new Thread(new GitLogRunnable(event, new ArrayList(connections)))).start();
	}

	class GitLogRunnable implements Runnable {
		Map<DiffEntry, String> entryList;
		List<GitWebSocket> socketList;
		String jsonString = "[]";

		public GitLogRunnable(Map<DiffEntry, String> entryList, List<GitWebSocket> socketList) {
			this.entryList = entryList;
			this.socketList = socketList;
		}

		public void run() {
			JsonFactory jsonFactory = new JsonFactory();
			StringWriter sw = new StringWriter();
			try {
				JsonGenerator jg = jsonFactory.createGenerator(sw);
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
