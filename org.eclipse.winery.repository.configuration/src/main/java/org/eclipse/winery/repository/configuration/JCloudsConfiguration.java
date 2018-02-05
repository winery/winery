/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.configuration;

import java.util.Objects;

public class JCloudsConfiguration {

    private String identity;
    private String credential;
    private String location;
    private String containerName;
    private String endPoint;

    public JCloudsConfiguration(String identity, String credential, String location, String containerName, String endPoint) {
        this.identity = Objects.requireNonNull(identity);
        this.credential = Objects.requireNonNull(credential);
        this.location = Objects.requireNonNull(location);
        this.containerName = Objects.requireNonNull(containerName);
        this.endPoint = Objects.requireNonNull(endPoint);
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

}
