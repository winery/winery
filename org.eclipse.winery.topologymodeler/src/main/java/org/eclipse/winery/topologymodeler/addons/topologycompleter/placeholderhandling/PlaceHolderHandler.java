/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.addons.topologycompleter.placeholderhandling;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer.TOSCAAnalyzer;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Constants;

/**
 * This class finds suitable replacement types for a place holder.
 */
public class PlaceHolderHandler {

	/**
	 * This method returns a suitable {@link TNodeType} to replace a given {@link TNodeTemplate} placeholder.
	 * A suitable Node Type to replace a placeholder is matched by its type. If the type of a NodeType equals the identifier of a placeholder
	 * it can be used to replace it.
	 *
	 * @param nodeTemplate
	 *            the placeholder to be replaced
	 * @param toscaAnalyzer
	 *            the {@link TOSCAAnalyzer} object to access the data model
	 *
	 * @return a list of {@link TNodeType}s to replace the placeholder
	 */
	public static List<TNodeType> getSuitableNodeTypes(TNodeTemplate nodeTemplate, TOSCAAnalyzer toscaAnalyzer) {

		List<TNodeType> suitableNodeTypes = new ArrayList<>();

		// TODO: matching the name without a name space is unsafe and only works assuming that no one creates generic NodeTemplates with the same name as the place holders.
		// However a NodeTemplate name does not have a name space.
		if (nodeTemplate.getName().equals(Constants.PlaceHolders.WEBSERVER.toString())) {
			for (TNodeType nodeType : toscaAnalyzer.getNodeTypes()) {
				if (nodeType.getDerivedFrom() != null && nodeType.getDerivedFrom().getTypeRef().getLocalPart().equals(Constants.PlaceHolders.WEBSERVER.toString())) {
					suitableNodeTypes.add(nodeType);
				}
			}
		} else if (nodeTemplate.getName().equals(Constants.PlaceHolders.DATABASE.toString())) {
			for (TNodeType nodeType : toscaAnalyzer.getNodeTypes()) {
				if (nodeType.getDerivedFrom() != null && nodeType.getDerivedFrom().getTypeRef().getLocalPart().equals(Constants.PlaceHolders.DATABASE.toString())) {
					suitableNodeTypes.add(nodeType);
				}
			}
		} else if (nodeTemplate.getName().equals(Constants.PlaceHolders.OPERATINGSYSTEM.toString())) {
			for (TNodeType nodeType : toscaAnalyzer.getNodeTypes()) {
				if (nodeType.getDerivedFrom() != null && nodeType.getDerivedFrom().getTypeRef().getLocalPart().equals(Constants.PlaceHolders.OPERATINGSYSTEM.toString())) {
					suitableNodeTypes.add(nodeType);
				}
			}
		} else if (nodeTemplate.getName().equals(Constants.PlaceHolders.CLOUDPROVIDER.toString())) {
			for (TNodeType nodeType : toscaAnalyzer.getNodeTypes()) {
				if (nodeType.getDerivedFrom() != null && nodeType.getDerivedFrom().getTypeRef().getLocalPart().equals(Constants.PlaceHolders.CLOUDPROVIDER.toString())) {
					suitableNodeTypes.add(nodeType);
				}
			}
		}

		return suitableNodeTypes;
	}
}
