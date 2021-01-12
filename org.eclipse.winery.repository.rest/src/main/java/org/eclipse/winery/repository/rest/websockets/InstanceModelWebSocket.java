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
import java.util.Map;
import java.util.Set;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.winery.model.adaptation.instance.InstanceModelPluginChooser;
import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/refineInstanceModel")
public class InstanceModelWebSocket extends AbstractWebSocket implements InstanceModelPluginChooser {

    private static final Logger logger = LoggerFactory.getLogger(InstanceModelWebSocket.class);
    private Session session;

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        // todo
    }
    
    @Override
    public InstanceModelRefinementPlugin selectPlugin(Set<InstanceModelRefinementPlugin> plugins) {
        return null;
    }

    @Override
    public Map<String, String> getUserInputs(Set<String> requiredInputs) {
        return null;
    }
}
