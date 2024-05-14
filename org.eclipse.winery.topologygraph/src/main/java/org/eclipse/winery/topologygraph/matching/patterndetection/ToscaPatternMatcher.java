/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.matching.ToscaPrmPropertyMatcher;

public abstract class ToscaPatternMatcher extends ToscaPrmPropertyMatcher {

    protected final OTPatternRefinementModel prm;
    private final Map<QName, ? extends TEntityType> entityTypes = new HashMap<>();

    public ToscaPatternMatcher(OTPatternRefinementModel prm, NamespaceManager namespaceManager) {
        super(namespaceManager);
        this.prm = prm;

        IRepository repository = RepositoryFactory.getRepository();
        this.entityTypes.putAll(repository.getQNameToElementMapping(NodeTypeId.class));
        this.entityTypes.putAll(repository.getQNameToElementMapping(RelationshipTypeId.class));
    }

    protected boolean isCompatible(TEntityTemplate left, TEntityTemplate right) {
        return ModelUtilities.isOfType(left.getType(), right.getType(), entityTypes)
            && characterizingPatternsCompatible(left, right);
    }
}
