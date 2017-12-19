/**
 * Copyright (c) 2017 Marvin Wohlfarth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.patterndetection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.patterndetection.keywords.Messaging;
import org.eclipse.winery.repository.patterndetection.keywords.OperatingSystem;
import org.eclipse.winery.repository.patterndetection.keywords.Server;
import org.eclipse.winery.repository.patterndetection.keywords.Service;
import org.eclipse.winery.repository.patterndetection.keywords.Storage;
import org.eclipse.winery.repository.patterndetection.keywords.VirtualHardware;
import org.eclipse.winery.repository.patterndetection.model.AbstractTopology;
import org.eclipse.winery.repository.patterndetection.model.PatternComponent;
import org.eclipse.winery.repository.patterndetection.model.PatternPosition;
import org.eclipse.winery.repository.patterndetection.model.RelationshipEdge;
import org.eclipse.winery.repository.patterndetection.model.TNodeTemplateExtended;
import org.eclipse.winery.repository.patterndetection.model.patterns.ElasticLoadBalancerPattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.ElasticQueuePattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.ElasticityManagerPattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.EnvironmentBasedAvailabilityPattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.ExecutionEnvironmentPattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.ExecutionEnvironmentPattern2;
import org.eclipse.winery.repository.patterndetection.model.patterns.KeyValueStoragePattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.MessageOrientedMiddlewarePattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.NodeBasedAvailabilityPattern;
import org.eclipse.winery.repository.patterndetection.model.patterns.RelationalDatabasePattern;
import org.eclipse.winery.repository.patterndetection.model.patterntaxonomies.IaaSTaxonomy;
import org.eclipse.winery.repository.patterndetection.model.patterntaxonomies.PaaSTaxonomy;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class Detection {

	private static final String propertiesFilename = "patterndetection.properties";

	private Properties properties;

	private String labelServer;
	private String labelService;
	private String labelOS;
	private String labelVirtualHardware;
	private String labelMessaging;
	private String labelStorage;

	private String keywordBeanstalk;
	private String keywordOpenstack;
	private String keywordEC2;
	private String keywordJava;
	private String keywordPython;
	private String keywordApache;
	private String keywordTomcat;
	private String keywordMosquitto;
	private String keywordMongoDB;
	private String keywordMySQL;

	private String patternEnvBasedAvail;
	private String patternElasticLoadBalancer;
	private String patternExecEnv;
	private String patternElasticityManager;
	private String patternElasticQueue;
	private String patternMessageMiddleware;
	private String patternNodeBasedAvail;
	private String patternRelationalDatabase;
	private String patternElasticInfrastructure;
	private String patternElasticPlatform;
	private String patternPaaS;
	private String patternIaaS;
	private String patternPublicCloud;
	private String patternKeyValueStorage;

	// intially both boolean values are set to false, isIaas is set to true if any virtual hardware is detected, isPaaS is set to true if anything on top of the virtual hardware level (such as: server, application, etc.) is detected
	private boolean isPaaS;
	private boolean isIaaS;

	// list with pattern names, which are detected
	private List<String> detectedPattern = new ArrayList<>();
	private List<String> impossiblePattern = new ArrayList<>();

	// this list contains all keywords detected any name of a node template
	private List<String> matchedKeywords = new ArrayList<>();

	// lists with pattern probablilities
	private List<String> patternProbabilityHigh = new ArrayList<>();
	private List<String> patternProbabilityMedium = new ArrayList<>();
	private List<String> patternProbabilityLow = new ArrayList<>();

	// this list contains all NodeTemplates, which are identified via keywords
	private List<TNodeTemplateExtended> labeledNodeTemplates = new ArrayList<>();

	private PaaSTaxonomy paas = new PaaSTaxonomy();
	private IaaSTaxonomy iaas = new IaaSTaxonomy();
	private SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> paasGraph;
	private SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> iaasGraph;
	private AbstractTopology abstractTopology;
	private TNodeTemplate basisNodeTemplate;

	private ServiceTemplateId serviceTemplateId;

	// this list holds the information about the patterns and their correspondent nodes in a topology graph
	private List<PatternPosition> patternPositions;

	public Detection(ServiceTemplateId serviceTemplateId) {
		patternPositions = new ArrayList<>();
		this.serviceTemplateId = serviceTemplateId;
		properties = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		isPaaS = false;
		isIaaS = false;

		labelServer = properties.getProperty("labelServer");
		labelService = properties.getProperty("labelService");
		labelOS = properties.getProperty("labelOS");
		labelVirtualHardware = properties.getProperty("labelVirtualHardware");
		labelMessaging = properties.getProperty("labelMessaging");
		labelStorage = properties.getProperty("labelStorage");

		keywordBeanstalk = properties.getProperty("keywordBeanstalk");
		keywordEC2 = properties.getProperty("keywordEC2");
		keywordOpenstack = properties.getProperty("keywordOpenstack");
		keywordJava = properties.getProperty("keywordJava");
		keywordPython = properties.getProperty("keywordPython");
		keywordApache = properties.getProperty("keywordApache");
		keywordTomcat = properties.getProperty("keywordTomcat");
		keywordMongoDB = properties.getProperty("keywordMongoDB");
		keywordMySQL = properties.getProperty("keywordMySQL");
		keywordMosquitto = properties.getProperty("keywordMosquitto");

		patternElasticLoadBalancer = properties.getProperty("nodeElasticLoadBalancer");
		patternEnvBasedAvail = properties.getProperty("nodeEnvBasedAv");
		patternExecEnv = properties.getProperty("nodeExecEnv");
		patternElasticityManager = properties.getProperty("nodeElasticityManager");
		patternElasticQueue = properties.getProperty("nodeElasticQueue");
		patternNodeBasedAvail = properties.getProperty("nodeNodeBasedAv");
		patternRelationalDatabase = properties.getProperty("nodeRelationalDatabase");
		patternMessageMiddleware = properties.getProperty("nodeMessaging");
		patternElasticPlatform = properties.getProperty("nodeElasticPlatform");
		patternElasticInfrastructure = properties.getProperty("nodeElasticInfrastructure");
		patternIaaS = properties.getProperty("nodeIaaS");
		patternPaaS = properties.getProperty("nodePaaS");
		patternPublicCloud = properties.getProperty("nodePublicCloud");
		patternKeyValueStorage = properties.getProperty("nodeKeyValue");
	}

	public List<List<String>> detectPattern() {
		ServiceTemplateResource serviceTempateResource = (ServiceTemplateResource) AbstractComponentsResource.getComponentInstaceResource(serviceTemplateId);
		TTopologyTemplate tTopologyTemplate = serviceTempateResource.getServiceTemplate().getTopologyTemplate();
		searchForKeywords(tTopologyTemplate);
		detectPattern(tTopologyTemplate);
		List<List<String>> listLists = new ArrayList<>();
		listLists.add(detectedPattern);
		listLists.add(patternProbabilityHigh);
		listLists.add(patternProbabilityMedium);
		listLists.add(patternProbabilityLow);
		listLists.add(impossiblePattern);
		return listLists;
	}

	public List<PatternPosition> getPatternPositions() {
		return patternPositions;
	}

	/**
	 * 1. step: search for keywords using predefined keywords in enums
	 */
	private void searchForKeywords(TTopologyTemplate tTopologyTemplate) {
		List<TNodeTemplate> tNodeTemplateList = ModelUtilities.getAllNodeTemplates(tTopologyTemplate);
		List<Server> serverList = new ArrayList<>(EnumSet.allOf(Server.class));
		List<Service> serviceList = new ArrayList<>(EnumSet.allOf(Service.class));
		List<VirtualHardware> virtualHardwareList = new ArrayList<>(EnumSet.allOf(VirtualHardware.class));
		List<OperatingSystem> operatingSystemList = new ArrayList<>(EnumSet.allOf(OperatingSystem.class));
		List<Messaging> messagingList = new ArrayList<>(EnumSet.allOf(Messaging.class));
		List<Storage> storageList = new ArrayList<>(EnumSet.allOf(Storage.class));

		for (TNodeTemplate tNodeTemplate : tNodeTemplateList) {
			for (Server server : serverList) {
				if (tNodeTemplate.getName().toLowerCase().contains(server.toString().toLowerCase())) {
					// add the matching keyword
					matchedKeywords.add(server.toString());
					// create a new TNodeTemplateExtended with the detected keyword and set the according label
					TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, labelServer, server.toString());
					// add this object to the list with labeled NodeTemplates
					labeledNodeTemplates.add(temp);
					isPaaS = true;
				}
			}
			for (Service service : serviceList) {
				if (tNodeTemplate.getName().toLowerCase().contains(service.toString().toLowerCase())) {
					matchedKeywords.add(service.toString());
					TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, labelService, service.toString());
					labeledNodeTemplates.add(temp);
					isPaaS = true;
				}
			}
			for (VirtualHardware virtualHardware : virtualHardwareList) {
				if (tNodeTemplate.getName().toLowerCase().contains(virtualHardware.toString().toLowerCase())) {
					matchedKeywords.add(virtualHardware.toString());
					TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, labelVirtualHardware, virtualHardware.toString());
					labeledNodeTemplates.add(temp);
					isIaaS = true;
				}
			}
			for (OperatingSystem operatingSystem : operatingSystemList) {
				if (tNodeTemplate.getName().toLowerCase().contains(operatingSystem.toString().toLowerCase())) {
					matchedKeywords.add(operatingSystem.toString());
					TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, labelOS, operatingSystem.toString());
					labeledNodeTemplates.add(temp);
					isPaaS = true;
				}
			}
			for (Messaging messaging : messagingList) {
				if (tNodeTemplate.getName().toLowerCase().contains(messaging.toString().toLowerCase())) {
					matchedKeywords.add(messaging.toString());
					TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, labelMessaging, messaging.toString());
					labeledNodeTemplates.add(temp);
					isPaaS = true;
				}
			}
			for (Storage storage : storageList) {
				if (tNodeTemplate.getName().toLowerCase().contains(storage.toString().toLowerCase())) {
					matchedKeywords.add(storage.toString());
					TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, labelStorage, storage.toString());
					labeledNodeTemplates.add(temp);
					isPaaS = true;
				}
			}
		}

		// create all taxonomies
		paasGraph = paas.getPaasTaxonomie();
		iaasGraph = iaas.getIaasTaxonomie();

		// set propabilities for possible patterns according to detected keywords only if any keywords matched the node template names
		if (!matchedKeywords.isEmpty()) {
			// for each specific keyowrds like beanstalk or mysql, probabilities are set in the pattern taxonomy graph
			for (String keyword : matchedKeywords) {
				DefaultWeightedEdge tempEdge;
				if (keyword.equals(keywordBeanstalk)) {
					detectedPattern.add(patternPaaS);
					tempEdge = paasGraph.getEdge(paas.getElasticPlatform(), paas.getEnvBasedAv());
					// set 100% probability of the pattern this edge is pointing to
					paasGraph.setEdgeWeight(tempEdge, 0.99);
					tempEdge = paasGraph.getEdge(paas.getElasticPlatform(), paas.getElasticLoadBalancer());
					paasGraph.setEdgeWeight(tempEdge, 0.99);
					tempEdge = paasGraph.getEdge(paas.getElasticPlatform(), paas.getElasticityManager());
					paasGraph.setEdgeWeight(tempEdge, 0.99);
				} else if (keyword.equals(keywordOpenstack) || keyword.equals(keywordEC2)) {
					tempEdge = iaasGraph.getEdge(iaas.getIaas(), iaas.getElasticInfrastructure());
					iaasGraph.setEdgeWeight(tempEdge, 0.75);
				} else if (keyword.equals(keywordMySQL)) {
					tempEdge = paasGraph.getEdge(paas.getPaas(), paas.getRelationalDatabase());
					paasGraph.setEdgeWeight(tempEdge, 0.99);
					tempEdge = paasGraph.getEdge(paas.getPaas(), paas.getKeyValueStorage());
					paasGraph.setEdgeWeight(tempEdge, 0.0);
				} else if (keyword.equals(keywordJava) || keyword.equals(keywordPython) || keyword.equals(keywordTomcat) || keyword.equals(keywordApache)) {
					tempEdge = paasGraph.getEdge(paas.getPaas(), paas.getExecEnvironment());
					paasGraph.setEdgeWeight(tempEdge, 0.75);
				} else if (keyword.equals(keywordMongoDB)) {
					tempEdge = paasGraph.getEdge(paas.getPaas(), paas.getKeyValueStorage());
					paasGraph.setEdgeWeight(tempEdge, 0.99);
					tempEdge = paasGraph.getEdge(paas.getPaas(), paas.getRelationalDatabase());
					paasGraph.setEdgeWeight(tempEdge, 0.0);
				}
			}

			// this case indicates only IaaS keywords and no detected PaaS keywords -> IaaS is assumed, therefore PaaS is added to impossible patterns
			if (isIaaS && !isPaaS) {
				detectedPattern.add(patternIaaS);
				impossiblePattern.add(patternPaaS);
				impossiblePattern.add(patternElasticPlatform);
				Set<DefaultWeightedEdge> edgeSet = iaasGraph.edgeSet();
				Iterator iterator2 = edgeSet.iterator();
				// iterate through the IaaS taxonomy and check the weight of each edge -> add the target nodes to the according pattern list
				while (iterator2.hasNext()) {
					DefaultWeightedEdge edge = (DefaultWeightedEdge) iterator2.next();
					double weight = iaasGraph.getEdgeWeight(edge);
					if (weight == 0.75) {
						patternProbabilityHigh.add(iaasGraph.getEdgeTarget(edge));
					} else if (weight == 0.5) {
						patternProbabilityMedium.add(iaasGraph.getEdgeTarget(edge));
					} else if (weight == 0.25) {
						patternProbabilityLow.add(iaasGraph.getEdgeTarget(edge));
					} else if (weight == 0.99) {
						detectedPattern.add(iaasGraph.getEdgeTarget(edge));
					} else if (weight == 0.0) {
						impossiblePattern.add(iaasGraph.getEdgeTarget(edge));
					} else if (weight == 1.0) {
						//for all other patterns add low probability, 1.0 is default edge value
						patternProbabilityLow.add(iaasGraph.getEdgeTarget(edge));
					}
				}

			// this case occurs if IaaS and PaaS keywords are detected or just PaaS keywords -> PaaS is assumed and excludes IaaS
			} else {
				detectedPattern.add(patternPaaS);
				impossiblePattern.add(patternIaaS);
				impossiblePattern.add(patternElasticInfrastructure);
				Set<DefaultWeightedEdge> edgeSet;
				edgeSet = paasGraph.edgeSet();
				Iterator iterator = edgeSet.iterator();
				// iterate through the IaaS taxonomy and check the weight of each edge -> add the target nodes to the according pattern list
				while (iterator.hasNext()) {
					DefaultWeightedEdge edge = (DefaultWeightedEdge) iterator.next();
					double weight = paasGraph.getEdgeWeight(edge);
					if (weight == 0.75) {
						patternProbabilityHigh.add(paasGraph.getEdgeTarget(edge));
					} else if (weight == 0.5) {
						patternProbabilityMedium.add(paasGraph.getEdgeTarget(edge));
					} else if (weight == 0.25) {
						patternProbabilityLow.add(paasGraph.getEdgeTarget(edge));
					} else if (weight == 0.99) {
						detectedPattern.add(paasGraph.getEdgeTarget(edge));
					} else if (weight == 0.0) {
						impossiblePattern.add(paasGraph.getEdgeTarget(edge));
					} else if (weight == 1.0) {
						//for all other patterns add low probability, 1.0 is default edge value
						patternProbabilityLow.add(paasGraph.getEdgeTarget(edge));
					}
				}
			}
		}
	}

	/**
	 * 2. step: Create all subgraphs of the topology graph and test for isomorphism with pattern graphs
	 * @param tTopologyTemplate: the TOSCA topology will be labeled
	 */
	private void detectPattern(TTopologyTemplate tTopologyTemplate) {
		abstractTopology = new AbstractTopology(tTopologyTemplate, labeledNodeTemplates);

		List<TNodeTemplate> tNodeTemplateList = ModelUtilities.getAllNodeTemplates(tTopologyTemplate);
		List<TRelationshipTemplate> tRelationshipTemplateList = ModelUtilities.getAllRelationshipTemplates(tTopologyTemplate);
		getLowestNode(tNodeTemplateList.get(0), tRelationshipTemplateList);

		Set<TNodeTemplateExtended> allNodes = abstractTopology.getGraph().vertexSet();
		TNodeTemplateExtended baseNodeExtended = new TNodeTemplateExtended();
		Iterator iterator = allNodes.iterator();
		// search for the lowest node in the abstract topology graph, this is used to copy the lowest node from the original Topology to the AbstractTopology
		while (iterator.hasNext()) {
			TNodeTemplateExtended temp = (TNodeTemplateExtended) iterator.next();
			if (temp.getNodeTemplate().getId().equals(basisNodeTemplate.getId())) {
				baseNodeExtended = temp;
				break;
			}
		}
		// map the topology outgoing from the given base node
		abstractTopology.map(baseNodeExtended);

		// in the patternList all graphs of the pattern objects are added
		List<DirectedGraph<PatternComponent, RelationshipEdge>> patternList = new ArrayList<>();
		HashMap<Integer, String> patternNames = new HashMap<>();

		// create objects of all known patterns
		ExecutionEnvironmentPattern executionEnvironmentPattern = new ExecutionEnvironmentPattern();
		NodeBasedAvailabilityPattern nodeBasedAvailabilityPattern = new NodeBasedAvailabilityPattern();
		ElasticityManagerPattern elasticityManagerPattern = new ElasticityManagerPattern();
		ElasticLoadBalancerPattern elasticLoadBalancerPattern = new ElasticLoadBalancerPattern();
		ElasticQueuePattern elasticQueuePattern = new ElasticQueuePattern();
		EnvironmentBasedAvailabilityPattern environmentBasedAvailabilityPattern = new EnvironmentBasedAvailabilityPattern();
		MessageOrientedMiddlewarePattern messageOrientedMiddlewarePattern = new MessageOrientedMiddlewarePattern();
		RelationalDatabasePattern relationalDatabasePattern = new RelationalDatabasePattern();
		KeyValueStoragePattern keyValueStoragePattern = new KeyValueStoragePattern();
		ExecutionEnvironmentPattern2 executionEnvironmentPattern2 = new ExecutionEnvironmentPattern2();

		// to receive the right pattern name for the current counter, pattern names are associated with numbers
		patternNames.put(0, patternExecEnv);
		patternNames.put(1, patternNodeBasedAvail);
		patternNames.put(2, patternElasticityManager);
		patternNames.put(3, patternElasticLoadBalancer);
		patternNames.put(4, patternElasticQueue);
		patternNames.put(5, patternEnvBasedAvail);
		patternNames.put(6, patternMessageMiddleware);
		patternNames.put(7, patternRelationalDatabase);
		patternNames.put(8, patternKeyValueStorage);
		patternNames.put(9, patternExecEnv);

		// pattern are added in order
		patternList.add(executionEnvironmentPattern.getPatternGraph());
		patternList.add(nodeBasedAvailabilityPattern.getPatternGraph());
		patternList.add(elasticityManagerPattern.getPatternGraph());
		patternList.add(elasticLoadBalancerPattern.getPatternGraph());
		patternList.add(elasticQueuePattern.getPatternGraph());
		patternList.add(environmentBasedAvailabilityPattern.getPatternGraph());
		patternList.add(messageOrientedMiddlewarePattern.getPatternGraph());
		patternList.add(relationalDatabasePattern.getPatternGraph());
		patternList.add(keyValueStoragePattern.getPatternGraph());
		patternList.add(executionEnvironmentPattern2.getPatternGraph());

		int countIndex = 0;
		// abstractTopology represents the base graph, for each pattern graph search for a subgraph isomorphism between base graph & pattern graph
		for (DirectedGraph<PatternComponent, RelationshipEdge> pattern : patternList) {
			VF2SubgraphIsomorphismInspector<TNodeTemplateExtended, RelationshipEdge> inspector = new VF2SubgraphIsomorphismInspector(abstractTopology.getGraph(), pattern);
			if (inspector.isomorphismExists()) {
				Iterator it = inspector.getMappings();
				while (it.hasNext()) {
					IsomorphicGraphMapping mapping = (IsomorphicGraphMapping) it.next();

					// list for counting all matches between pattern nodes and base graph nodes, must be true for all
					List<Boolean> matched = new ArrayList<>();

					// this graph holds the nodes of the base graph in which the pattern occurs
					DirectedGraph<TNodeTemplateExtended, RelationshipEdge> originGraph = new SimpleDirectedGraph<>(RelationshipEdge.class);

					// each node of the pattern graph is compared to the according node in the GraphMapping
					for (PatternComponent p : pattern.vertexSet()) {
						//check if matched subgraph and topology have the same components, get the correspondent vertex in the mapping for a node
						TNodeTemplateExtended v = (TNodeTemplateExtended) mapping.getVertexCorrespondence(p, false);

						// if the names equal, the node is added to the originGraph and a boolean with value true is added to the matched list
						if (p.getName().equals(v.getLabel())) {
							matched.add(true);
							originGraph.addVertex(v);

						} else {
							matched.add(false);
						}
					}

					// correspondent to the countIndex, the pattern name is retrieved
					if (!matched.contains(false) && !impossiblePattern.contains(patternNames.get(countIndex))) {
						// add a new pattern position: the pattern name & the subgraph in which it occurs, this graph was built up in the previous step
						PatternPosition temp = new PatternPosition(patternNames.get(countIndex), originGraph);
						patternPositions.add(temp);
						detectedPattern.add(patternNames.get(countIndex));

						// sett some additional probabilities
						if (patternNames.get(countIndex).equals(patternEnvBasedAvail)) {
							patternProbabilityHigh.add(patternPublicCloud);
						} else if (patternNames.get(countIndex).equals(patternEnvBasedAvail)) {
							impossiblePattern.add(patternNodeBasedAvail);
						} else if (patternNames.get(countIndex).equals(patternNodeBasedAvail)) {
							impossiblePattern.add(patternEnvBasedAvail);
						}
					}
				}
			}
			countIndex++;
		}
	}

	/**
	 * Get the lowest node in a topology, this is the only node with any outgoing relation
	 */
	private void getLowestNode(TNodeTemplate baseNodeTemplate, List<TRelationshipTemplate> tRelationshipTemplateList) {
		List<TRelationshipTemplate> outgoing = new ArrayList<>();
		List<TRelationshipTemplate> incoming = new ArrayList<>();
		for (TRelationshipTemplate tRelationshipTemplate : tRelationshipTemplateList) {
			if (baseNodeTemplate.equals((tRelationshipTemplate.getSourceElement().getRef()))) {
				outgoing.add(tRelationshipTemplate);
				getLowestNode((TNodeTemplate) tRelationshipTemplate.getTargetElement().getRef(), tRelationshipTemplateList);
				break;
			} else {
				incoming.add(tRelationshipTemplate);
			}
		}
		if (outgoing.isEmpty()) {
			// lowestNode is set
			basisNodeTemplate = baseNodeTemplate;
		}
	}
}
