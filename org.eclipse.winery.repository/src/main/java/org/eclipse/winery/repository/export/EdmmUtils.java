/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.export;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.edmm.EdmmType;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.toscalite.ToscaLiteChecker;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EdmmUtils {

    public static Map<String, Object> checkToscaLiteCompatibility(TServiceTemplate serviceTemplate) {
        IRepository repository = RepositoryFactory.getRepository();

        Map<QName, TRelationshipType> relationshipTypes = repository.getQNameToElementMapping(RelationshipTypeId.class);
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        Map<QName, EdmmType> typeMap = repository.getEdmmManager().getTypeMap();
        Map<QName, EdmmType> oneToOneMap = repository.getEdmmManager().getOneToOneMap();

        ToscaLiteChecker toscaLiteChecker = new ToscaLiteChecker(nodeTypes, relationshipTypes, typeMap, oneToOneMap);

        boolean toscaLiteCompliant = toscaLiteChecker.isToscaLiteCompliant(serviceTemplate);
        Map<QName, List<String>> errorList = toscaLiteChecker.getErrorList();
        errorList.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        HashMap<String, Object> map = new HashMap<>();
        map.put("isToscaLiteCompatible", toscaLiteCompliant);
        map.put("errorList", errorList);

        return map;
    }
}
