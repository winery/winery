/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.addons.topologycompleter.analyzer;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Constants;
import org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class analyzes the occurrence of place holders in a topology and writes them to an {@link ArrayList}.
 */
public class PlaceHolderAnalyzer {

    /**
     * This method searches {@link TNodeTemplate}s that are derived from the abstract "PlaceHolder" type and adds them to a list.
     *
     * @param toscaAnalyzer the {@link TOSCAAnalyzer} object to access the data model
     * @return the found place holders of the topology as a list.
     */
    public static List<TNodeTemplate> analyzePlaceHolders(TOSCAAnalyzer toscaAnalyzer) {

        List<TNodeTemplate> foundPlaceHolders = new ArrayList<TNodeTemplate>();

        // Check the type of the NodeTemplates, write them to a list if the type is derived from the common place holder type.
        for (TNodeTemplate nodeTemplate : toscaAnalyzer.getNodeTemplates()) {

            TNodeType nodeType = Utils.getNodeTypeForId(toscaAnalyzer.getNodeTypes(), nodeTemplate.getType());

            if (nodeType != null && nodeType.getDerivedFrom() != null && nodeType.getDerivedFrom().getTypeRef().getLocalPart().equals(Constants.PLACE_HOLDER_QNAME.getLocalPart()) &&
                nodeType.getDerivedFrom().getTypeRef().getNamespaceURI().equals(Constants.PLACE_HOLDER_QNAME.getNamespaceURI())) {
                foundPlaceHolders.add(nodeTemplate);
            }
        }
        return foundPlaceHolders;
    }
}
