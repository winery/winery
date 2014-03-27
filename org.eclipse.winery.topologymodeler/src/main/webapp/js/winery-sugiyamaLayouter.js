/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Kálmán Képes - initial API and implementation and/or initial documentation
 *******************************************************************************/

/**
 * Implements abstractLayouter#layout and uses the Sugiyama method to apply a
 * layered layout for TOSCA Topology Template graphs. The Sugiyama method
 * proposes four steps to apply the layout:<br>
 * First: Remove the cycles temporarly of the given graph by finding edges that
 * are members of a cycle and reverse them<br>
 * Second: Layer the nodes by using DFS/BFS and reduce the "height"/"width" of
 * the graph<br>
 * Third: Reduce the crossings of edges, by applying a suitable heuristic <br>
 * Fourth: Assign the coordinates for the nodes/edges
 *
 * @param {Array}
 *            nodeTemplates, an array with div-elements of class
 *            ".NodeTemplateShape"
 */
define(function() {

	var module = {
		layout: layout
	};
	return module;

	function layout(nodeTemplates) {


		// Step 1: "Remove" Cycles
		var hasCycle = _hasCycle(nodeTemplates);
		var complementEdgesOfSubgraph = null;
		if (hasCycle){
			var edgesOfAcyclicSubgraph = _computeAcyclicSubgraph(nodeTemplates);
			complementEdgesOfSubgraph = _computeEdgeComplement(nodeTemplates,
				edgesOfAcyclicSubgraph);
			_reverseEdges(complementEdgesOfSubgraph);
		}

		// Step 2: Assign Layers
		_assignLayers(nodeTemplates);

		// Step 3: reduce crossing of edges
		_reduceCrossing(nodeTemplates);

		// Step 4: assign coordinates
		_assignCoordinates(nodeTemplates);

		if(hasCycle){
			_reverseEdges(complementEdgesOfSubgraph);
		}

		jsPlumb.repaintEverything();
		// if used twice, edges are drawn straight and not random
		jsPlumb.repaintEverything();

		// cleanup -> remote internal data
		$(nodeTemplates).removeAttr("layer");
		$(nodeTemplates).removeAttr("layerPos");

	}


	function callsOnLayout(nodeTemplates) {
		// TODO start sugiyama layout method with special handling of calls-on
		// relations
	}

	/**
	 * Returns true if the given graph contains a cycle
	 * @param nodeTemplates the vertices of the graph, where cycle detection should be performed. The vertices should be an array of NodeTemplates
	 * @returns {Boolean} true iff a cycle was detected in the graph, else false
	 */
	function _hasCycle(nodeTemplates) {
		for(var i = 0; i < nodeTemplates.length;i++){
			nodeTemplates[i].cycleMark = "notStarted";
		}

		var hasCycle = false;
		var sources = _returnSources(nodeTemplates);

		for(var i = 0; i < sources.length; i++){
			_cycleSearch(sources[i],hasCycle);
			if(hasCycle){
				// remove marks
				$(nodeTemplates).removeAttr("cycleMark");
				return hasCycle;
			}
		}

		return hasCycle;
	}

	var tarjanCounter = 1;

	/**
	 * Detects cycles which are reachable by the given NodeTemplate of a graph. Algorithm is based on DFS
	 * @param nodeTemplate the starting NodeTemplate for the DFS
	 * @param hasCycle boolean variable to propagate if a cycle was found, init value should be 'false'
	 */
	function _cycleSearch(nodeTemplate,hasCycle){
		if(nodeTemplate.cycleMark = "inWork"){
			hasCycle =true;
		} else if(nodeTemplate.cycleMark ="notStarted") {
			nodeTemplate.cycleMark ="inWork";
			var successors = _returnSuccessors(nodeTemplate);
			for(var i = 0 ; i < successors.length; i++){
				_cycleSearch(successors[i]);
			}
			nodeTemplate.cycleMark ="finished";
		}

	}

	/**
	 * Calculates strongly connected components according to Tarjans algorithm
	 * @param nodeTemplate a NodeTemplate that is used to begin the algorithm on
	 * @param foundCycle boolean variable to propagate if a cycle was found, init value should be 'false'
	 */
	function _findComponent(nodeTemplate, foundCycle){
		if(nodeTemplate.cycleMark == "inWork"){
			foundCycle = true;
		} else if(nodeTemplate.cycleMark == "notStarted"){
			nodeTemplate.cycleMark = "inWork";
			nodeTemplate.dfsNum = tarjanCounter;
			nodeTemplate.compNum = tarjanCounter;
			tarjanCounter++;
			var successors = _returnSuccessors(nodeTemplate);
			for(var i = 0; i < successors.length;i++){
				var successor = successors[i];
				if(successor.cycleMark != "finished"){
					_findComponent(successor,foundCycle);
					if(successor.compNum < nodeTemplate.compNum){
						nodeTemplate.compNum = successor.compNum;
					}
				}
			}
			nodeTemplate.cycleMark = "finished";
		}
	}

	/**
	 * Assigns x/y-Coordinates to the given NodeTemplates. It is assumed that the
	 * NodeTemplates are already layered, e.g. nodeTemplate.layer,
	 * nodeTemplate.layerPos is set.
	 *
	 * @param nodeTemplates
	 *            the nodetemplate to set it's x/y coordinates
	 */
	function _assignCoordinates(nodeTemplates) {
		// get the layers out of the graph and the number of layers
		var highestLayerNumber = _returnHighestLayerNumber(nodeTemplates);
		var layerMap = {};
		for ( var i = 1; i <= highestLayerNumber; i++) {
			layerMap[i.toString()] = _returnLayer(nodeTemplates, i);
		}

		// determine the layer with the greatest width, width = {max(|U|) : U is
		// layer of V}
		var widestLayerWidth = -1;
		for (layer in layerMap) {
			if (layer.length > widestLayerWidth) {
				widestLayerWidth = layer.length;
			}
		}

		// get maximum node width
		var maxNodeWidth = _getMaxWidthOfNodeArray(nodeTemplates);

		// get maximum node height
		var maxNodeHeight = _getMaxHeightOfNodeArray(nodeTemplates);

		var editorArea = $("#editorArea")[0];
		var height = editorArea.clientHeight;
		var width = editorArea.clientWidth;

		// TODO fine tuning or change the algorithm to handle callsOn Relations
		// independently, e.g. draw it in an own tree/list/graph
		// distance in height between nodes
		var prettyHeight = 75;
		for ( var i = highestLayerNumber; i > 0; i--) {
			var layer = _returnLayer(nodeTemplates, i);
			// distance in width between nodes
			var prettyWidth = 75;
			for ( var nodeIndex = 0; nodeIndex < layer.length; nodeIndex++) {
				var layerNumber = i;
				var layerPos = layer[nodeIndex].layerPos;
				layer[nodeIndex].style.left = ((layerPos) * maxNodeWidth + prettyWidth)
						+ "px";
				layer[nodeIndex].style.top = ((highestLayerNumber - layerNumber)
						* maxNodeHeight + prettyHeight)
						+ "px"
				prettyWidth += 50;
			}
			prettyHeight += 100;
		}
	}

	/**
	 * Returns the height of the nodeTemplate with the highest height
	 * @param nodeTemplates an array of NodeTemplates
	 * @returns {Number} the maximum height of the given NodeTemplates
	 */
	function _getMaxHeightOfNodeArray(nodeTemplates) {
		var maxHeight = -1;
		for ( var i = 0; i < nodeTemplates.length; i++) {
			// TODO clientHeight responds to the complete page, not only the
			// nodetemplate
			if (maxHeight < nodeTemplates[i].clientHeight) {
				maxHeight = nodeTemplates[i].clientHeight;
			}
		}
		return maxHeight;
	}

	/**
	 * Returns the width of the nodeTemplate with the highest width
	 * @param nodeTemplates an array of NodeTemplates
	 * @returns {Number} the maximum width of the given NodeTemplates
	 */
	function _getMaxWidthOfNodeArray(nodeTemplates) {
		var maxWidth = -1;
		for ( var i = 0; i < nodeTemplates.length; i++) {
			// TODO clientWidth responds to the complete page, not only the
			// nodetemplate
			if (maxWidth < nodeTemplates[i].clientWidth) {
				maxWidth = nodeTemplates[i].clientWidth;
			}
		}
		return maxWidth;
	}

	/**
	 * Reduces crossing between the already layered Graph, given as a Array of NodeTemplates
	 * @param nodeTemplates the vertices of the layered graph, to reduce crossing between the layers
	 */
	function _reduceCrossing(nodeTemplates) {
		// initialize arbitrary orderings inside layers
		_initLayerOrder(nodeTemplates);
		var maxlayerlevel = _returnHighestLayerNumber(nodeTemplates);
		// variable to reduce crossings of layer-pairs one by one
		var layerCount = 1;
		// variable to check if some change was made
		var changed = true;
		while (layerCount < maxlayerlevel | changed) {
			// the algorithm should run until no position changes are made
			if (layerCount == maxlayerlevel) {
				layerCount = 1;
			}
			changed = false;

			var layer = _returnLayer(nodeTemplates, layerCount + 1);

			// INFO: This piece of code reduces the corssing according to the
			// barycenter heuristic
			for ( var i = 0; i < layer.length; i++) {
				// deg(u), where u = layer[i]
				var deg_u = _returnIncomingEdges(layer[i]).length
						+ _returnOutgoingEdges(layer[i]).length;

				// sum of layerpos(v), for ever v where (u,v) exists in the graph
				var outgoingEdges = _returnOutgoingEdges(layer[i]);
				var sum = 0.0;
				for ( var j = 0; j < outgoingEdges.length; j++) {
					var node_v = outgoingEdges[j].target;
					sum = sum + node_v.layerPos;
				}

				// barycenter heuristic
				var bary_u = 1 / deg_u * sum;
				layer[i].barycenter = bary_u;
			}

			// sorting the nodetemplates in the layer accorindg to the barycenter
			layer.sort(function(a, b) {
				return a.barycenter - b.barycenter
			});

			// rearrange positions in layer
			for ( var i = 0; i < layer.length; i++) {
				if (layer[i].layerPos != i + 1) {
					// checks whether some change of the position is made
					changed = true;
				}
				layer[i].layerPos = i + 1;
			}

			// get ready for the next layer
			layerCount++;
		}
	}

	/**
	 * Returns an array of NodeTemplates which are in the same Layer (nodeTemplate.layer)
	 */
	function _returnLayer(nodeTemplates, layerNumber) {
		var layer = new Array();
		for ( var i = 0; i < nodeTemplates.length; i++) {
			if ("layer" in nodeTemplates[i]
					&& nodeTemplates[i].layer == layerNumber) {
				layer.push(nodeTemplates[i]);
			}
		}
		layer.sort(function(a, b) {
			return a.layerPos - b.layerPos;
		});
		return layer;
	}

	/**
	 * Returns the number of the highest layer of the given layered graph
	 * @param nodeTemplates the vertices of the layered graph as an array of NodeTemplates
	 * @returns {Number} the highest layer number
	 */
	function _returnHighestLayerNumber(nodeTemplates) {
		var layerNumber = -1;
		for ( var i = 0; i < nodeTemplates.length; i++) {
			if (!"layer" in nodeTemplates[i]) {
				// some node has no "layer" property defined <=> graph isn't layered
				return -1;
			} else {
				if (nodeTemplates[i].layer > layerNumber) {
					layerNumber = nodeTemplates[i].layer;
				}
			}
		}
		return layerNumber;
	}

	/**
	 * Initializes a layering order in each layer of the given graph
	 * @param nodeTemplates the vertices of the graph to layer, as an array of NodeTemplates
	 */
	function _initLayerOrder(nodeTemplates) {
		// "map" := layerNumber : ordernumber
		var orderingCount = {};
		// init with 1 : 1
		orderingCount["1"] = 1;
		for ( var i = 0; i < nodeTemplates.length; i++) {
			// get layer number of the nodetemplate
			var layerNumber = nodeTemplates[i].layer;
			// is the layer already in the map ?
			if (layerNumber.toString() in orderingCount) {
				// if yes, set the pos inside layer and increment for next node
				nodeTemplates[i].layerPos = orderingCount[layerNumber.toString()];
				orderingCount[layerNumber.toString()] = orderingCount[layerNumber
						.toString()] + 1;
			} else {
				// if not, set the pos to 1 and add layer to map with count 2
				nodeTemplates[i].layerPos = 1;
				// init pos for next node
				orderingCount[layerNumber.toString()] = 2;
			}
		}
	}

	/**
	 * Assigns layer numbers to the given graph
	 * @param nodeTemplates the vertices of the graph to assign layers to, as an array of NodeTemplates
	 */
	function _assignLayers(nodeTemplates) {
		var sinks = {};
		for ( var i = 0; i < nodeTemplates.length; i++) {
			if (_returnOutgoingEdges(nodeTemplates[i]).length == 0) {
				sinks[i] = nodeTemplates[i];
			}
		}

		// array of DIV delements
		var nodesToCompute = new Array();

		for ( var sink in sinks) {
			// set layer level for the sinks and add the nodes which are reachable
			// from there
			sinks[sink].layer = 1;
			var inEdges = _returnIncomingEdges(sinks[sink]);
			for ( var i = 0; i < inEdges.length; i++) {
				nodesToCompute.push(inEdges[i].source);
			}
		}

		// set other layer levels
		while (nodesToCompute.length != 0) {
			var node = nodesToCompute.shift();
			var layerNumber = _returnLayerNumber(node);
			if (layerNumber == -1) {
				nodesToCompute.push(node);
			} else {
				node.layer = layerNumber;
				// add ingoing nodes to compute
				var edgesToNodes = _returnIncomingEdges(node);
				for ( var i = 0; i < edgesToNodes.length; i++) {
					nodesToCompute.push(edgesToNodes[i].source);
				}
			}
		}
	}

	/**
	 * Returns the number of the layer a NodeTemplate belongs to
	 * @param nodeTemplate a NodeTemplate with already set layer position
	 * @returns {Number} the number of the layer the NodeTemplate belongs to
	 */
	function _returnLayerNumber(nodeTemplate) {
		var layerNumber = -1;
		var outgoingEdges = _returnOutgoingEdges(nodeTemplate);
		for ( var i = 0; i < outgoingEdges.length; i++) {
			// we will see if it works here
			var successorNode = outgoingEdges[i].target;
			if ("layer" in successorNode) {
				if (successorNode.layer + 1 >= layerNumber) {
					layerNumber = successorNode.layer + 1;
				}
			} else {
				layerNumber = -1;
				break;
			}
		}
		return layerNumber;
	}

	/**
	 * Returns all jsPlumb#Connection edges of the graph represented by the
	 * nodeTemplates array of NodeTemplateShape which aren't referenced in the
	 * subgraphEdges array of type jsPlumb#Connection.
	 *
	 * @param {Object}
	 *            nodeTemplates, array of NodeTemplateShape
	 * @param {Object}
	 *            subgraphEdges, array of jsPlumb#Connection
	 * @return {Object} array of jsPlumb#Connection which contains edges that aren't
	 *         in the subgraph
	 */
	function _computeEdgeComplement(nodeTemplates, subgraphEdges) {
		var edgeSet = {};
		for ( var i = 0; i < nodeTemplates.length; i++) {
			var incomingEdges = _returnIncomingEdges(nodeTemplates[i]);
			var outgoingEdges = _returnOutgoingEdges(nodeTemplates[i]);
			for ( var incomingEdgeIndex = 0; incomingEdgeIndex < incomingEdges.length; incomingEdgeIndex++) {
				var inSubgraph = false;
				for ( var subgraphEdgeIndex = 0; subgraphEdgeIndex < subgraphEdges.length; subgraphEdgeIndex++) {
					if (incomingEdges[incomingEdgeIndex] === subgraphEdges[subgraphEdgeIndex]) {
						inSubgraph = true;
						break;
					}
				}
				if (!inSubgraph) {
					edgeSet[incomingEdges[incomingEdgeIndex]] = incomingEdges[incomingEdgeIndex];
				}
			}
			for ( var outgoingEdgeIndex = 0; outgoingEdgeIndex < outgoingEdges.length; outgoingEdgeIndex++) {
				var inSubgraph = false;
				for ( var subgraphEdgeIndex = 0; subgraphEdgeIndex < subgraphEdges.length; subgraphEdgeIndex++) {
					if (outgoingEdges[outgoingEdgeIndex] === subgraphEdges[subgraphEdgeIndex]) {
						inSubgraph = true;
						break;
					}
				}
				if (!inSubgraph) {
					edgeSet[outgoingEdges[outgoingEdgeIndex]] = outgoingEdges[outgoingEdgeIndex];
				}
			}
		}
		// transform edge-set to array
		var edgeArray = new Array();
		for (edge in edgeSet) {
			edgeArray.push(edgeSet[edge]);
		}
		return edgeArray;
	}

	/**
	 * Returns a list of edges which represent an acyclic subgraph of the given
	 * nodes. The graph is at least of size 1/2*|E|, which can be troublesome in
	 * some situations. This means the graph could be layouted only on the "half" of
	 * it.
	 *
	 * Info: There are more sophisticated methods that guarantee to produce bigger
	 * subgraphs, see "Drawing Graphs: Methods and Models". But in this state it is
	 * a huge undertake, cause Oryx doesn't seem to have any basic graph algorithm
	 * shipped, like DFS etc.(correct me if i'm wrong, please).
	 *
	 * @param {Object}
	 *            nodes of a graph, which are NodeTemplatesShapes
	 * @return [Object] edges of the contained acyclic subgraph, which are
	 *         jsPlumb#Connection Objects
	 * @see "Drawing Graphs: Methods and Models", p. 91
	 */
	function _computeAcyclicSubgraph(nodeTemplates) {
		var subgraphEdges = [];
		for ( var i = 0; i < nodeTemplates.length; i++) {
			if (_returnOutgoingEdges(nodeTemplates[i]).length >= _returnIncomingEdges(nodeTemplates[i]).length) {
				for ( var j = 0; j < _returnOutgoingEdges(nodeTemplates[i]).length; j++) {
					subgraphEdges.push(_returnOutgoingEdges(nodeTemplates[i])[j]);
				}
			} else {
				for ( var j = 0; j < _returnIncomingEdges(nodeTemplates[i]).length; j++) {
					subgraphEdges.push(_returnIncomingEdges(nodeTemplates[i])[j]);
				}
			}
		}
		var filteredArray = subgraphEdges.filter(function(elem, pos) {
			return subgraphEdges.indexOf(elem) == pos;
		});
		return filteredArray;
	}

	/**
	 * Returns relations having the given NodeTemplate as target
	 *
	 * @param nodeTemplate
	 *            a nodeTemplateShape
	 * @returns Returns an array containing jsPlumb#Connection objects representing
	 *          relations
	 */
	function _returnIncomingEdges(nodeTemplate) {
		var edgeArray = new Array();
		jsPlumb.select().each(
				function(connection) {
					if (connection.targetId === nodeTemplate.id) {
						/*if (connection.getType().length != 0) {
							for ( var i = 0; i < connection.getType().length; i++) {
								if (connection.getType()[i].toLowerCase().indexOf(
										"hostedon") !== -1) {
									edgeArray.push(connection);
								}
							}
						} else {
							edgeArray.push(connection);
						}*/
						edgeArray.push(connection);

					}
				});
		return edgeArray;
	}

	/**
	 * Returns relations having the given NodeTemplate as source
	 *
	 * @param nodeTemplate
	 *            a nodeTemplateShape
	 * @returns Returns an array containing jsPlumb#Connections objects representing
	 *          relations
	 */
	function _returnOutgoingEdges(nodeTemplate) {
		var edgeArray = new Array();
		jsPlumb.select().each(
				function(connection) {
					if (connection.sourceId === nodeTemplate.id) {
						/*if (connection.getType().length != 0) {
							for ( var i = 0; i < connection.getType().length; i++) {
								if (connection.getType()[i].toLowerCase().indexOf(
										"hostedon") !== -1) {
									edgeArray.push(connection);
								}
							}
						} else {
							edgeArray.push(connection);
						}*/
						edgeArray.push(connection);
					}
				});
		return edgeArray;
	}

	/**
	 * Returns the NodeTemplates which can be reached by a outgoing edges of the given NodeTemplate
	 * @param nodeTemplate the NodeTemplate whose Succesors should be calculated
	 * @returns {Array} an array of NodeTemplates which are successors of the given NodeTemplate, may be empty
	 */
	function _returnSuccessors(nodeTemplate){
		var nodeArray = new Array();
		var outgoingEdges = _returnOutgoingEdges(nodeTemplate);
		for(var i =0; i < outgoingEdges.length; i++){
			nodeArray.push(outgoingEdges[i].target);
		}
		return nodeArray;
	}

	/**
	 * Returns all sources of the given graph
	 * @param nodeTemplates vertices of the graph, as an array of NodeTemplates
	 * @returns {Array} an Array of NodeTemplates
	 */
	function _returnSources(nodeTemplates){
		var sourceArray = new Array();
		for(var i = 0; i < nodeTemplates.length; i++){
			if(_returnIncomingEdges(nodeTemplates[i]) == 0){
				sourceArray.push(nodeTemplates[i]);
			}
		}
		return sourceArray;
	}

	/**
	 * Reverses a relationshipTemplate
	 *
	 * @param relationshipTemplate
	 *            as we don't have relationshiptemplates modelled here we expect
	 *            jsPlumb#Connection objects
	 */
	function _reverseEdge(relationshipTemplate) {
		// this seems to work
		var source = relationshipTemplate["source"];
		var sourceId = relationshipTemplate["sourceId"];
		var target = relationshipTemplate["target"];
		var targetId = relationshipTemplate["targetId"];

		relationshipTemplate["source"] = target;
		relationshipTemplate["sourceId"] = targetId;
		relationshipTemplate["target"] = source;
		relationshipTemplate["targetId"] = sourceId;
	}

	/**
	 * Reverses relationshipTemplates
	 *
	 * @param relationshipTemplates
	 */
	function _reverseEdges(relationshipTemplates) {
		for ( var i = 0; i < relationshipTemplates.length; i++) {
			_reverseEdge(relationshipTemplates[i]);
		}
	}
});