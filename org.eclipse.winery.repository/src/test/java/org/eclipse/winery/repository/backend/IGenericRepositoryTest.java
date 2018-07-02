/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.backend;

import org.eclipse.winery.common.ids.definitions.*;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IGenericRepositoryTest extends TestWithGitBackedRepository {

    @Test
    public void getReferencedDefinitionsChildIds() throws Exception {
        this.setRevisionTo("origin/plain");
        final ServiceTemplateId serviceTemplateWithFourPolicies = new ServiceTemplateId("http%3A%2F%2Fplain.winery.opentosca.org%2Fservicetemplates", "ServiceTemplateWithFourPolicies", true);
        final Collection<DefinitionsChildId> referencedDefinitionsChildIds = this.repository.getReferencedDefinitionsChildIds(serviceTemplateWithFourPolicies);
        
        NodeTypeId nodeTypeWithoutPropertiesId = new NodeTypeId("http://plain.winery.opentosca.org/nodetypes", "NodeTypeWithoutProperties", false);
        PolicyTemplateId policyTemplateId = new PolicyTemplateId("http://plain.winery.opentosca.org/policytemplates", "PolicyTemplateWithoutProperties", false);
        PolicyTypeId policyTypeId = new PolicyTypeId("http://plain.winery.opentosca.org/policytypes", "PolicyTypeWithoutProperties", false);

        final Set<DefinitionsChildId> expected = new HashSet<>(Arrays.asList(nodeTypeWithoutPropertiesId, policyTemplateId, policyTypeId));
        assertEquals(expected, referencedDefinitionsChildIds);
    }
}
