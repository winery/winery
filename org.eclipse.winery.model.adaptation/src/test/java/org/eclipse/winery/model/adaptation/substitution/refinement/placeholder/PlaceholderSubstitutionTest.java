/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.refinement.placeholder;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

class PlaceholderSubstitutionTest extends TestWithGitBackedRepository {

    public PlaceholderSubstitutionTest() {
        super("https://github.com/saatkamp/diss-tosca-repository");
    }

    @Test
    public void placeholderSubstitution() throws GitAPIException {
        setRevisionTo("origin/main");

        QName serviceTemplateQNameDetector = new QName("http://opentosca.org/divamethod/tests", "Test-PlaceholderSubstitution_subgraphdetector-w1-wip1");
        ServiceTemplateId serviceTemplateIdDetector = new ServiceTemplateId(serviceTemplateQNameDetector);
        TTopologyTemplate subgraphDetector = this.repository.getElement(serviceTemplateIdDetector).getTopologyTemplate();

        PlaceholderSubstitution placeholderSubstitution = new PlaceholderSubstitution(
            new ServiceTemplateId("http://opentosca.org/divamethod/tests", "Test-PlaceholderSubstitution_gdm-w1-wip1-w1-wip1", false),
            subgraphDetector, null, "substituted");

        placeholderSubstitution.substitutePlaceholders();
    }
}
