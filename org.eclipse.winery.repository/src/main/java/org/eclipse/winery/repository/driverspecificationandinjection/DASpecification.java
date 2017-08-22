/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Karoline Saatkamp - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.winery.repository.driverspecificationandinjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class DASpecification {
		
	public static List<TNodeTemplate> getNodeTemplatesWithAbstractDAs (TTopologyTemplate topologyTemplate) {
		List<TNodeTemplate> nodeTemplates = topologyTemplate.getNodeTemplates();
		List<TNodeTemplate> nodeTemplatesWithAbstractDA = nodeTemplates.stream()
				.filter(nt -> nt.getDeploymentArtifacts() != null)
				.filter(nt -> nt.getDeploymentArtifacts().getDeploymentArtifact().stream()
						.anyMatch(da -> getArtifactTypeOfDA(da).getAbstract() == TBoolean.YES))
				.collect(Collectors.toList());
				
		return nodeTemplatesWithAbstractDA;
	}
	
	public static TArtifactType getArtifactTypeOfDA (TDeploymentArtifact deploymentArtifact) {
		QName DAArtifactTypeQName = deploymentArtifact.getArtifactType();
		ArtifactTypeId artifactTypeId = new ArtifactTypeId(DAArtifactTypeQName);
		TArtifactType artifactType = RepositoryFactory.getRepository().getElement(artifactTypeId);
		return artifactType;
	}
	
	public static List<TArtifactType> getArtifactTypeHierarchy (TArtifactType artifactType) {
		
		List<TArtifactType> artifactTypeHierarchy = new ArrayList<>();
		
		TArtifactType basisArtifactType = artifactType;
		artifactTypeHierarchy.add(basisArtifactType);
		
		while (basisArtifactType != null) {
			if (basisArtifactType.getDerivedFrom() != null) {
				QName parentArtifactTypeQName = basisArtifactType.getDerivedFrom().getTypeRef();
				ArtifactTypeId parentArtifactTypeId = new ArtifactTypeId(parentArtifactTypeQName);
				basisArtifactType = RepositoryFactory.getRepository().getElement(parentArtifactTypeId);
				artifactTypeHierarchy.add(basisArtifactType);
			} else {
				basisArtifactType = null;
			}
		}
		
		return artifactTypeHierarchy;
	}

	/**
	 * 
	 * @param nodeTemplate
	 * @param deploymentArtifact
	 * @param topologyTemplate
	 * @return
	 */
	public static Map<TRelationshipTemplate, TNodeTemplate> getNodesWithSuitableConcreteDAAndTheDirectlyConnectedNode 
			(TNodeTemplate nodeTemplate, TDeploymentArtifact deploymentArtifact, TTopologyTemplate topologyTemplate) {
		
		// key is the node template the nodeTemplate is directly connected to this is the indicator from which connection the concrete DA is coming from
		// value is the node template which has a concrete DA attached to substiute the abstract DA of the nodeTemplate
		Map<TRelationshipTemplate, TNodeTemplate> nodeTemplateWithConcreteDAAndDirectlyConnectedNode = new HashMap<>();
		List<TRelationshipTemplate> outgoingRelationshipTemplates = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);
		
		//concrete DAs could be find in the hostedOn stack or the connected stacks, but just in directly connected stacks
		for (TRelationshipTemplate outgoingRelationship: outgoingRelationshipTemplates) {
			TNodeTemplate targetNodeTemplate = ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, outgoingRelationship);
			//In each directly connected stack a node with matching concrete DA is looked up
			TNodeTemplate nodesWithSuitableDA = getNodesWithSuitableConcreteDAs(targetNodeTemplate, deploymentArtifact, topologyTemplate);
			if (nodesWithSuitableDA != null) {
				nodeTemplateWithConcreteDAAndDirectlyConnectedNode.put(outgoingRelationship, nodesWithSuitableDA);
			}
		}
		return nodeTemplateWithConcreteDAAndDirectlyConnectedNode;
	}

	/**
	 * 
	 * @param nodeTemplate
	 * @param deploymentArtifact
	 * @param topologyTemplate
	 * @return
	 */
	public static TNodeTemplate getNodesWithSuitableConcreteDAs (TNodeTemplate nodeTemplate, TDeploymentArtifact deploymentArtifact, TTopologyTemplate topologyTemplate) {
		// Checks the attached DAs to the node template and compare them with the abstract Type of the D
		if (getSuitableConcreteDA(deploymentArtifact, nodeTemplate) == null) {
			//A concrete DA can only be found in the hostedOn stack
			List<TRelationshipTemplate> outgoingRelationshipTemplates = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate)
					.stream().filter(outrel -> getBasisRelationshipType(outrel.getType()).getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("Container"))
					.collect(Collectors.toList());
				
			for (TRelationshipTemplate relationshipTemplate : outgoingRelationshipTemplates) {
				TNodeTemplate targetNodeTemplate = ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, relationshipTemplate);
				return getNodesWithSuitableConcreteDAs(targetNodeTemplate, deploymentArtifact, topologyTemplate);
			}
				
		} else {
			return nodeTemplate;
		}
		return null;
	}
	
	public static TDeploymentArtifact getSuitableConcreteDA(TDeploymentArtifact abstractDeploymentArtifact, TNodeTemplate nodeTemplate) {
		TDeploymentArtifact concreteDA = null;
		if (nodeTemplate.getDeploymentArtifacts() != null) {
			List<TDeploymentArtifact> concreteDeploymentArtifacts = nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact();
			concreteDA = getConcreteDA(abstractDeploymentArtifact, concreteDeploymentArtifacts);
		} 
		if (concreteDA == null) {
			List<TNodeTypeImplementation> nodeTypeImplementations = getmatchingNodeTypeImplementations(nodeTemplate.getType());
			for (TNodeTypeImplementation nodeTypeImplementation : nodeTypeImplementations) {
				if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
					List<TDeploymentArtifact> deploymentArtifacts = nodeTypeImplementation.getDeploymentArtifacts().getDeploymentArtifact();
					concreteDA = getConcreteDA(abstractDeploymentArtifact, deploymentArtifacts);
					
					if (concreteDA != null) {
						return concreteDA;
					}
				}
			}
		} else {
			return concreteDA;
		}
		return null;
	}
	
	private static List<TNodeTypeImplementation> getmatchingNodeTypeImplementations (QName nodeTypeQName) {
		final IRepository repository = RepositoryFactory.getRepository();
		return repository.getAllTOSCAComponentIds(NodeTypeImplementationId.class).stream()
				.map(id -> repository.getElement(id))	
				.filter(nti -> nti.getNodeType().equals(nodeTypeQName))
				.collect(Collectors.toList());
	}
	
	private static TDeploymentArtifact getConcreteDA(TDeploymentArtifact abstractDeploymentArtifact, List<TDeploymentArtifact> candidates) {
		for (TDeploymentArtifact candidate : candidates) {
			List<TArtifactType> artifactTypeHierarchy = getArtifactTypeHierarchy(getArtifactTypeOfDA(candidate));
			TArtifactType abstractArtifactType = getArtifactTypeOfDA(abstractDeploymentArtifact);
			for (TArtifactType containedArtifacts : artifactTypeHierarchy) {
				//Can not be realized with streams because a check if the abstract Type is contained in the hierarchy doesn't work
				if (containedArtifacts.getTargetNamespace().equals(abstractArtifactType.getTargetNamespace())
						&& containedArtifacts.getName().equals(abstractArtifactType.getName())) {
					return candidate;
				}
			}
		}
		return null;
	}

	/**
	 * method already exists in Splitting. Put into ModelUtilities
	 * @param relationshipTypeQName
	 * @return
	 */
	private static TRelationshipType getBasisRelationshipType(QName relationshipTypeQName) {
		RelationshipTypeId parentRelationshipTypeId = new RelationshipTypeId(relationshipTypeQName);
		TRelationshipType parentRelationshipType = RepositoryFactory.getRepository().getElement(parentRelationshipTypeId);
		TRelationshipType basisRelationshipType = null;

		while (parentRelationshipType != null) {
			basisRelationshipType = parentRelationshipType;

			if (parentRelationshipType.getDerivedFrom() != null) {
				QName tempRelationshipTypeQName = parentRelationshipType.getDerivedFrom().getTypeRef();
				parentRelationshipTypeId = new RelationshipTypeId(tempRelationshipTypeQName);
				parentRelationshipType = RepositoryFactory.getRepository().getElement(parentRelationshipTypeId);
			} else {
				parentRelationshipType = null;
			}
		}
		return basisRelationshipType;
	}
}
