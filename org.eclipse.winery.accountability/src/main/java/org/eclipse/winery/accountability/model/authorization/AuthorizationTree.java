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
package org.eclipse.winery.accountability.model.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.rutledgepaulv.prune.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The tree-of-trust. Each node in the tree represents an authorized participant, and each edge represents the authorization
 * operation (participant authorizing another). The tree is rooted at the service owner.
 * The tree is resilient to:
 * (i) the addition of the same participant more than once -the addition oldest remains-
 * (ii) the existence of participants not connected with the service owner -they are discarded-.
 */
public class AuthorizationTree implements AuthorizationInfo {
    /**
     * The tree itself doesn't store the identity of the service owner, so we have a default name.
     */
    private static final String SERVICE_OWNER_IDENTITY = "Service Owner";
    private static final Logger log = LoggerFactory.getLogger(AuthorizationTree.class);
    private Tree<AuthorizationNode> tree;

    public AuthorizationTree(List<AuthorizationElement> allAuthorizationElements) {
        tree = buildTree(allAuthorizationElements);
        Objects.requireNonNull(tree);
    }

    @Override
    public boolean isAuthorized(String blockchainAddress) {
        final Optional<AuthorizationNode> result = tree.breadthFirstSearch(node ->
            node.getAddress().equals(blockchainAddress));

        return result.isPresent();
    }

    @Override
    public Optional<String> getServiceOwnerBlockchainAddress() {
        final Optional<AuthorizationNode> root = tree.depthFirstStream().findFirst();
        // convert Optional<AuthorizationNode> to Optional<String>
        return root.map(AuthorizationNode::getAddress);
    }

    @Override
    public Optional<String> getRealWorldIdentity(String blockchainAddress) {
        final Optional<AuthorizationNode> result = tree.breadthFirstSearch(node ->
            node.getAddress().equals(blockchainAddress));

        return result.map(AuthorizationNode::getIdentity);
    }

    @Override
    public Optional<List<AuthorizationNode>> getAuthorizationLineage(String blockchainAddress) {
        // Find a strand that contains a node with the given blockchain address as authorized (each strand is ordered from
        // root to leaf)
        Optional<List<AuthorizationNode>> strandAsList =
            tree.getStrands()
                .map(strand -> strand.collect(Collectors.toList()))
                .filter(strand -> strand.stream().anyMatch(node -> node.getAddress().equals(blockchainAddress)))
                .findFirst();

        // If we find the strand, build a path from node to root
        if (strandAsList.isPresent()) {
            final List<AuthorizationNode> result = new ArrayList<>();

            for (AuthorizationNode node : strandAsList.get()) {
                result.add(node);

                // add nodes up until the one we need
                if (node.getAddress().equals(blockchainAddress))
                    break;
            }

            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    private Tree<AuthorizationNode> buildTree(List<AuthorizationElement> elements) {

        if (elements.size() > 0) {
            AuthorizationNode serviceOwner = new AuthorizationNode(elements.get(0), elements.get(0).getAuthorizerBlockchainAddress(),
                SERVICE_OWNER_IDENTITY);
            final Map<String, AuthorizationNode> nodesMap = new HashMap<>();
            final List<String> edges = new ArrayList<>();

            nodesMap.put(serviceOwner.getAddress(), serviceOwner);
            elements.forEach(item -> {
                // the authorizer must already be included; otherwise they are not authorized in the first place!
                if (nodesMap.containsKey(item.getAuthorizerBlockchainAddress())) {
                    // the authorized must not already be included; otherwise they are already authorized! 
                    if (!nodesMap.containsKey(item.getAuthorizedBlockchainAddress())) {
                        AuthorizationNode currentSource = nodesMap.get(item.getAuthorizerBlockchainAddress());
                        AuthorizationNode currentTarget = new AuthorizationNode(item, item.getAuthorizedBlockchainAddress(),
                            item.getAuthorizedIdentity());
                        nodesMap.put(currentTarget.getAddress(), currentTarget);
                        // this works while addresses are unique!
                        edges.add(currentSource.getAddress() + currentTarget.getAddress());
                    } else {
                        log.debug(
                            "Participant (" + item.getAuthorizerBlockchainAddress() +
                                ") tried to re-authorize the participant: (" + item.getAuthorizedBlockchainAddress() +
                                ") which was already authorized before!");
                    }
                } else {
                    log.debug(
                        "Unauthorized participant (" + item.getAuthorizerBlockchainAddress() +
                            ") tried to authorize the participant: (" + item.getAuthorizedBlockchainAddress() + ")");
                }
            });
            // Create all potential trees from the list, there has to be only one!
            List<Tree<AuthorizationNode>> trees = Tree.of(nodesMap.values(),
                (parent, child) -> edges.contains(parent.getData().getAddress() + child.getData().getAddress())
            );

            assert (trees.size() == 1);

            return trees.get(0);
        }

        log.error("Failed to build tree-of-trust: Input is empty!");
        return null;
    }

    @Override
    public String toString() {
        return tree.toString();
    }
}
