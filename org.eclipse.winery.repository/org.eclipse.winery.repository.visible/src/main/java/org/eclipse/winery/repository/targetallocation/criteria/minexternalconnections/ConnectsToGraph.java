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

package org.eclipse.winery.repository.targetallocation.criteria.minexternalconnections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

/**
 * Creates a simplified graph out of a topology to be used with {@link KargerMinCutVariation}.
 * Only Node Templates are considered which have no predecessors and are connected via
 * connectsTo Relationship Templates.
 *
 * The graph only contains absolutely necessary information:
 * the IDs and Target Labels of the Node Templates and the Relationship Templates between them.
 * This way, the {@link KargerMinCutVariation} algorithm can be executed with less overhead.
 *
 * This class also provides a copy constructor for the whole graph and individual nodes.
 */
public class ConnectsToGraph {

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private List<Edge> edgesForContraction = new ArrayList<>();

    public ConnectsToGraph(TopologyWrapper topology) {
        Map<String, Node> nodesByIds = new HashMap<>();

        for (TNodeTemplate topLevelNT : topology.getTopLevelNTs()) {
            Optional<String> targetLabel = ModelUtilities.getTargetLabel(topLevelNT);
            Node node;
            if (targetLabel.isPresent()) {
                node = new Node(topLevelNT.getId(), targetLabel.get());
            } else {
                node = new Node(topLevelNT.getId());
            }
            nodes.add(node);
            nodesByIds.put(topLevelNT.getId(), node);
        }

        for (TRelationshipTemplate connectsToRT : topology.getConnectsTos(topology.getRelationshipTemplates())) {
            Node source = nodesByIds.get(topology.getSourceNT(connectsToRT).getId());
            Node target = nodesByIds.get(topology.getTargetNT(connectsToRT).getId());
            Edge edge = new Edge(source, target);

            edges.add(edge);
            if (edge.isContractible()) {
                edgesForContraction.add(edge);
            }
        }
    }

    public ConnectsToGraph(ConnectsToGraph connectsToGraph) {
        Map<Set<String>, Node> nodesByIds = new HashMap<>();

        for (Node node : connectsToGraph.getNodes()) {
            Node newNode = new Node(node);
            nodes.add(newNode);
            nodesByIds.put(newNode.getNodeTemplateIds(), newNode);
        }

        for (Edge edge : connectsToGraph.getEdges()) {
            Node source = nodesByIds.get(edge.getSource().getNodeTemplateIds());
            Node target = nodesByIds.get(edge.getTarget().getNodeTemplateIds());
            Edge newEdge = new Edge(source, target);

            edges.add(newEdge);
            if (newEdge.isContractible()) {
                edgesForContraction.add(newEdge);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectsToGraph that = (ConnectsToGraph) o;
        // workaround: HashSet doesn't support mutable objects
        return Objects.equals(new HashSet<>(nodes), new HashSet<>(that.nodes));
    }

    @Override
    public int hashCode() {
        // workaround: HashSet doesn't support mutable objects
        return Objects.hash(new HashSet<>(nodes));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Edge> getEdgesForContraction() {
        return edgesForContraction;
    }

    public static class Node {
        private Set<String> nodeTemplateIds;
        private String targetLabel;

        public Node(String nodeTemplateId, String targetLabel) {
            this.nodeTemplateIds = new HashSet<>();
            this.nodeTemplateIds.add(nodeTemplateId);
            this.targetLabel = targetLabel;
        }

        public Node(String nodeTemplateId) {
            this.nodeTemplateIds = new HashSet<>();
            this.nodeTemplateIds.add(nodeTemplateId);
            this.targetLabel = "";
        }

        public Node(Node node) {
            this.nodeTemplateIds = new HashSet<>(node.getNodeTemplateIds());
            this.targetLabel = node.getTargetLabel();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(nodeTemplateIds, node.nodeTemplateIds) &&
                Objects.equals(targetLabel, node.targetLabel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeTemplateIds, targetLabel);
        }

        public Set<String> getNodeTemplateIds() {
            return nodeTemplateIds;
        }

        public void addNodeTemplateIds(Set<String> nodeTemplateIds) {
            this.nodeTemplateIds.addAll(nodeTemplateIds);
        }

        public String getTargetLabel() {
            return targetLabel;
        }

        public void setTargetLabel(String targetLabel) {
            this.targetLabel = targetLabel;
        }
    }

    public static class Edge {
        private Node source;
        private Node target;

        public Edge(Node source, Node target) {
            this.source = source;
            this.target = target;
        }

        public boolean isContractible() {
            return (source.getTargetLabel().equals("") || target.getTargetLabel().equals("")) ||
                source.getTargetLabel().equals(target.getTargetLabel());
        }

        public Node getSource() {
            return source;
        }

        public void setSource(Node source) {
            this.source = source;
        }

        public Node getTarget() {
            return target;
        }

        public void setTarget(Node target) {
            this.target = target;
        }
    }
}
