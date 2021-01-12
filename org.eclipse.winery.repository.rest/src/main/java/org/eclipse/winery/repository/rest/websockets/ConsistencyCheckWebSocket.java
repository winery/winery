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

import java.io.IOException;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.winery.common.json.JacksonProvider;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyChecker;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerConfiguration;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyCheckerProgressListener;
import org.eclipse.winery.repository.backend.consistencycheck.ConsistencyErrorCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/checkconsistency")
public class ConsistencyCheckWebSocket extends AbstractWebSocket implements ConsistencyCheckerProgressListener {

    private static final Logger logger = LoggerFactory.getLogger(ConsistencyCheckWebSocket.class);

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        ConsistencyCheckerConfiguration config = JacksonProvider.mapper.readValue(message, ConsistencyCheckerConfiguration.class);

        logger.info("Start checking using config: {}", message);
        long startTime = System.currentTimeMillis();

        final ConsistencyChecker consistencyChecker = new ConsistencyChecker(config, this);
        consistencyChecker.checkCorruption();
        ConsistencyErrorCollector errorList = consistencyChecker.getErrorCollector();

        long duration = (System.currentTimeMillis() - startTime) / 1000;
        logger.info("Finished checking repository consistency! Duration: {}min, {}s", (int) duration / 60, duration % 60);

        // Transform object to JSON and send it.
        this.session.getBasicRemote().sendText(JacksonProvider.mapper.writeValueAsString(errorList));

        // Close the connection after the check has passed.
        onClose(session);
    }

    /**
     * Publishes the current checking progress to the client in JSON format.
     */
    @Override
    public void updateProgress(float progress) {
        this.session.getAsyncRemote().sendText("{\"progress\":" + progress + "}");
    }

    /**
     * Publishes the detailed checking progress to the customer in JSON format.
     */
    @Override
    public void updateProgress(float progress, String checkingDefinition) {
        this.session.getAsyncRemote().sendText("{"
            + "\"progress\":" + progress + ","
            + "\"currentlyChecking\":\"" + checkingDefinition + "\""
            + "}"
        );
    }
}
