/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.websockets;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebSocket.class);
    protected Session session;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        logger.info("Opened consistency check web-socket with id: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Closing session " + session.getId());
        this.session.close();
        this.session = null;
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        logger.trace("Error in session " + session.getId(), t);
    }
}
