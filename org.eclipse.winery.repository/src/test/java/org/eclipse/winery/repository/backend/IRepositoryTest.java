/*******************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IRepositoryTest extends TestWithGitBackedRepository {

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
    
    @Test
    public void getReferencedDefinitionsChildIDsWithEmptyArtifactRefs() throws GitAPIException {
        this.setRevisionTo("5fb45405cc983d157dc417142ad32b01880e48af");
        
        final NodeTypeId nodeTypeId = new NodeTypeId("http%3A%2F%2Fwinery.opentosca.org/test/ponyuniverse", "shetland_pony", true);
        final QName qName = nodeTypeId.getQName();
        Collection<NodeTypeImplementationId> allNodeTypeImplementations = this.repository.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTypeId.getQName());
        assertEquals(0, allNodeTypeImplementations.size());
        
    }
}
