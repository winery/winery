/*******************************************************************************
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
package org.eclipse.winery.provenance.model.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class AuthorizationTreeTest {

    private AuthorizationTree authorizationTree;

    @BeforeEach
    public void setUp() {
        List<AuthorizationElement> elements = new ArrayList<>();
        elements.add(new AuthorizationElement("aaaa", 1, 1,
            "0x1111", "0x2222", "Michael"));
        elements.add(new AuthorizationElement("bbbb", 2, 2,
            "0x2222", "0x3333", "Vladimir"));
        // duplicate participant with different parent
        elements.add(new AuthorizationElement("cccc", 3, 3,
            "0x1111", "0x3333", "Vova"));
        // unauthorized participant authorizing others!
        elements.add(new AuthorizationElement("dddd", 4, 4,
            "0xffff", "0xeeee", "Uwe"));
        elements.add(new AuthorizationElement("eeee", 5, 5,
            "0xffff", "0x2222", "Michael"));

        this.authorizationTree = new AuthorizationTree(elements);
    }

    @Test
    public void getEmptyTree() {
        try {
            new AuthorizationTree(new ArrayList<>());
            fail("Expected a NullPointerException");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void getServiceOwner() {
        assertTrue(this.authorizationTree.getServiceOwnerBlockchainAddress().isPresent());
        assertEquals("0x1111", this.authorizationTree.getServiceOwnerBlockchainAddress().get());
    }

    @Test
    public void authorizedParticipant() {
        assertTrue(this.authorizationTree.isAuthorized("0x3333"));
    }

    @Test
    public void checkIdentity() {
        final Optional<String> realWorldIdentity = this.authorizationTree.getRealWorldIdentity("0x3333");

        assertTrue(realWorldIdentity.isPresent());
        assertEquals("Vladimir", realWorldIdentity.get());
    }

    // Also tests duplicate authorizations
    @Test
    public void getLineage() {
        final Optional<List<AuthorizationNode>> lineage =
            this.authorizationTree.getAuthorizationLineage("0x3333");

        assertTrue(lineage.isPresent());
        assertEquals(3, lineage.get().size());
        assertEquals("0x3333", ((AuthorizationNode) lineage.get().toArray()[lineage.get().size() - 1]).getAddress());
        assertEquals("0x2222", ((AuthorizationNode) lineage.get().toArray()[lineage.get().size() - 2]).getAddress());
        assertEquals("0x1111", ((AuthorizationNode) lineage.get().toArray()[lineage.get().size() - 3]).getAddress());
    }

    @Test
    public void unauthorizedParticipant() {
        assertFalse(this.authorizationTree.isAuthorized("0x5874"));
    }

    @Test
    public void multipleParticipantIdentities() {
        Optional<String> identity = this.authorizationTree.getRealWorldIdentity("0x3333");
        assertTrue(identity.isPresent());
        assertEquals("Vladimir", identity.get());
    }

    @Test
    public void unauthorizedAuthorization() {
        assertFalse(this.authorizationTree.isAuthorized("0xffff"));
        assertFalse(this.authorizationTree.isAuthorized("0xeeee"));

        final Optional<List<AuthorizationNode>> lineage =
            this.authorizationTree.getAuthorizationLineage("0x2222");
        assertTrue(lineage.isPresent());
        assertEquals("0x1111", ((AuthorizationNode) lineage.get().toArray()[lineage.get().size() - 2]).getAddress());
    }
}
