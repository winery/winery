/**
 * Copyright 2017 ZTE Corporation.
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

package org.eclipse.winery.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.List;

public class GatewayBranch {
	
	protected String id;
    private List<Node> nodeList = new ArrayList<Node>();

    public GatewayBranch() {
        super();
    }


	public List<Node> getNodeList() {
		return nodeList;
	}


	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    
    

}
