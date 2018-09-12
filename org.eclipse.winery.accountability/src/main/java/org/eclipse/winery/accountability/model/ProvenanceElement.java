/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.accountability.model;

import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;

public abstract class ProvenanceElement extends BlockchainElement {

    private String authorAddress;
    private String authorName;

    /**
     * True, if the author was authorized to be a certified contributor.
     */
    private boolean authorized;

    public ProvenanceElement(String transactionHash, long unixTimestamp, String authorAddress) {
        super(transactionHash, unixTimestamp);
        this.authorAddress = authorAddress.toLowerCase();
    }

    public ProvenanceElement() {
    }

    public String getAuthorAddress() {
        return authorAddress;
    }

    public void setAuthorAddress(String authorAddress) {
        this.authorAddress = authorAddress;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public void setAuthorizedFlag(AuthorizationInfo authorizationInfo) {
        if (authorizationInfo != null)
            this.setAuthorized(authorizationInfo.isAuthorized(authorAddress));
        else
            this.setAuthorized(false);
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
