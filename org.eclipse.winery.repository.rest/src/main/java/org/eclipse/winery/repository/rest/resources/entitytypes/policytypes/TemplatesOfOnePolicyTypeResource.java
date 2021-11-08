/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.policytypes;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.entitytypes.TemplatesOfOneType;

import java.util.Collection;

public class TemplatesOfOnePolicyTypeResource extends TemplatesOfOneType {

    private PolicyTypeId policyTypeId;

    /**
     * Resource returns all templates/implementations of the given policy type
     *
     * @param policyTypeId the Id of the policy type
     */
    public TemplatesOfOnePolicyTypeResource(PolicyTypeId policyTypeId) {
        this.policyTypeId = policyTypeId;
    }

    @Override
    public Collection<PolicyTemplateId> getAllImplementations() {
        return RepositoryFactory.getRepository().getAllElementsReferencingGivenType(PolicyTemplateId.class, this.policyTypeId.getQName());
    }
}
