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

package org.eclipse.winery.edmm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.edmm.model.EdmmType;
import org.eclipse.winery.edmm.toscalight.ToscaLightChecker;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EdmmUtils {

    public static Map<String, Object> checkToscaLightCompatibility(TServiceTemplate serviceTemplate) {
        ToscaLightChecker toscaLightChecker = getToscaLightChecker();

        boolean toscaLightCompliant = toscaLightChecker.isToscaLightCompliant(serviceTemplate);
        Map<QName, List<String>> errorList = toscaLightChecker.getErrorList();
        errorList.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        HashMap<String, Object> map = new HashMap<>();
        map.put("isToscaLightCompatible", toscaLightCompliant);
        map.put("errorList", errorList);

        return map;
    }

    public static ToscaLightChecker getToscaLightChecker() {
        IRepository repository = RepositoryFactory.getRepository();

        Map<QName, TRelationshipType> relationshipTypes = repository.getQNameToElementMapping(RelationshipTypeId.class);
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        Map<QName, EdmmType> typeMap = EdmmManager.forRepository(repository).getTypeMap();
        Map<QName, EdmmType> oneToOneMap = EdmmManager.forRepository(repository).getOneToOneMap();

        return new ToscaLightChecker(nodeTypes, relationshipTypes, typeMap, oneToOneMap);
    }

    public static Map<QName, TServiceTemplate> getAllToscaLightCompliantModels() {
        Map<QName, TServiceTemplate> serviceTemplates = RepositoryFactory.getRepository()
            .getQNameToElementMapping(ServiceTemplateId.class);

        ToscaLightChecker toscaLightChecker = EdmmUtils.getToscaLightChecker();

        return serviceTemplates.entrySet()
            .stream()
            .filter(entry -> toscaLightChecker.isToscaLightCompliant(entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
