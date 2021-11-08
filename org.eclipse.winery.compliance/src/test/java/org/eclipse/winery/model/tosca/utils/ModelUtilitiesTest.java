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

package org.eclipse.winery.model.tosca.utils;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModelUtilitiesTest extends TestWithGitBackedRepository {

    @Test
    public void getTargetLabel() {
        TNodeTemplate nodeTemplate = new TNodeTemplate();
        assertFalse(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());

        ModelUtilities.setTargetLabel(nodeTemplate, "");
        assertFalse(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());

        ModelUtilities.setTargetLabel(nodeTemplate, "UNDEFined");
        assertFalse(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());

        ModelUtilities.setTargetLabel(nodeTemplate, "TARGETLABEL");
        assertTrue(ModelUtilities.getTargetLabel(nodeTemplate).isPresent());
        assertEquals("targetlabel", ModelUtilities.getTargetLabel(nodeTemplate).get());
    }

    // region ********** isOfType **********

    @Test
    public void isOfType() {
        TEntityType.DerivedFrom derivedFrom = new TEntityType.DerivedFrom();
        derivedFrom.setType(QName.valueOf("{https://ex.org/nt}parent"));

        TNodeType nt1 = new TNodeType();
        nt1.setDerivedFrom(derivedFrom);

        HashMap<QName, TEntityType> map = new HashMap<>();
        map.put(QName.valueOf("{http://ex.org/nt}child"), nt1);

        assertTrue(
            ModelUtilities.isOfType(QName.valueOf("{https://ex.org/nt}parent"), QName.valueOf("{http://ex.org/nt}child"), map)
        );
    }

    // endregion

    @Test
    public void instantiateNodeTemplate() throws Exception {
        this.setRevisionTo("origin/plain");

        IRepository repository = RepositoryFactory.getRepository();
        TNodeType nodeType = repository.getElement(new NodeTypeId(QName.valueOf("{http://opentosca.org/add/management/to/instances/nodetypes}Ubuntu_16.04-w1")));

        TNodeTemplate nodeTemplate = ModelUtilities.instantiateNodeTemplate(nodeType);

        assertNotNull(nodeTemplate);
        assertNotNull(nodeTemplate.getProperties());
        assertEquals(8, ((TEntityTemplate.WineryKVProperties) nodeTemplate.getProperties()).getKVProperties().size());
    }
}
