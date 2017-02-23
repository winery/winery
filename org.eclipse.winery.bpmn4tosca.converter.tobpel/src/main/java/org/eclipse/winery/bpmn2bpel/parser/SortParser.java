/**
 * Copyright 2016-2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.winery.bpmn2bpel.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.model.Node;
import org.eclipse.winery.bpmn2bpel.model.OrGatewayMerge;
import org.eclipse.winery.bpmn2bpel.model.OrGatewaySplit;

public class SortParser {

    private Map<String, Node> nodeMap;

    private Map<String, Set<String>> nodeWithTargetsMap;

    public SortParser(Map<String, Node> nodeMap, Map<String, Set<String>> nodeWithTargetsMap) {
        super();
        this.nodeMap = nodeMap;
        this.nodeWithTargetsMap = nodeWithTargetsMap;
    }

    public SortParser() {
        super();
    }

    public ManagementFlow buildManagementFlow() {
        List<Node> nodeList = buildNodeList(getStartEvent().getId());
        ManagementFlow managementFlow = convert2ManagementFlow(nodeList);

        return managementFlow;
    }
    
    private Node getStartEvent() {
    	for(Node node : nodeMap.values()) {
    		if(JsonKeys.TASK_TYPE_START_EVENT.equals(node.getType())) {
    			return node;
    		}
    	}
    	
    	return null;
    }

    private ManagementFlow convert2ManagementFlow(List<Node> nodeList) {
        ManagementFlow managementFlow = new ManagementFlow();

        for (Node node : nodeList) {
            managementFlow.addVertex(node);
        }

        Node src = null;
        Node target = null;
        for (int i = 0; i < nodeList.size() - 1; i++) {
            src = nodeList.get(i);
            target = nodeList.get(i + 1);
            managementFlow.addEdge(src, target);
        }

        return managementFlow;
    }

    private List<Node> buildNodeList(String currentId) {
        List<Node> nodeList = new ArrayList<Node>();
        boolean hasNext = true;
        do {
        	Node node = nodeMap.get(currentId);
        	

            Set<String> followSet = nodeWithTargetsMap.get(currentId);

            if (node instanceof OrGatewaySplit) {
                String endGatewayId = addBranches4Gateway((OrGatewaySplit) node);
                nodeList.add(node);

                // loop for the next node
                Set<String> endFollowSet = nodeWithTargetsMap.get(endGatewayId);
                currentId = endFollowSet.iterator().next();
            } else if (node instanceof OrGatewayMerge) {
                hasNext = false;
            } else { // task node
            	
                nodeList.add(node);
                
                // the next node
                if (followSet == null || followSet.size() == 0) {
                    // final node
                    hasNext = false;
                } else {
                    currentId = followSet.iterator().next();
                }
            }
        } while (hasNext);
        return nodeList;
    }
    
    /**
     * add children nodes for each branch of gateway
     * @param gateway
     * @return
     */
    private String addBranches4Gateway(OrGatewaySplit gateway) {
        // 
        String endGatewayId = null;

        Iterator<String> iterator = nodeWithTargetsMap.get(gateway.getId()).iterator();

        while (iterator.hasNext()) {
            String currentId = iterator.next();

            List<Node> subList = buildNodeList(currentId);
            gateway.getBranch(currentId).setNodeList(subList);

            if (null == endGatewayId) {
                endGatewayId = getEndDecisionNodeId(subList);
            }
        }

        return endGatewayId;
    }

    private String getEndDecisionNodeId(List<Node> subList) {

    	Node lastTask = subList.get(subList.size() - 1);

        Set<String> followSet = nodeWithTargetsMap.get(lastTask.getId());
        return followSet.iterator().next();
    }
}
