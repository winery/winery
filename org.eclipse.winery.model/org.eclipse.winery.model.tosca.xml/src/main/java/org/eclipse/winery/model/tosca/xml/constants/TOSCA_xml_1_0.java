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

package org.eclipse.winery.model.tosca.xml.constants;

import org.eclipse.winery.model.tosca.xml.XTArtifactTemplate;
import org.eclipse.winery.model.tosca.xml.XTArtifactType;
import org.eclipse.winery.model.tosca.xml.XTCapabilityType;
import org.eclipse.winery.model.tosca.xml.XTExtensibleElements;
import org.eclipse.winery.model.tosca.xml.XTNodeType;
import org.eclipse.winery.model.tosca.xml.XTNodeTypeImplementation;
import org.eclipse.winery.model.tosca.xml.XTPolicyTemplate;
import org.eclipse.winery.model.tosca.xml.XTPolicyType;
import org.eclipse.winery.model.tosca.xml.XTRelationshipType;
import org.eclipse.winery.model.tosca.xml.XTRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.xml.XTRequirementType;
import org.eclipse.winery.model.tosca.xml.XTServiceTemplate;

public class TOSCA_xml_1_0 {
    @SuppressWarnings("unchecked")
    public static final Class<? extends XTExtensibleElements>[] DEFINITIONS_ELEMENT_CLASSES =
        new Class[] {
            XTServiceTemplate.class,
            XTNodeType.class,
            XTNodeTypeImplementation.class,
            XTRelationshipType.class,
            XTRelationshipTypeImplementation.class,
            XTRequirementType.class,
            XTCapabilityType.class,
            XTArtifactType.class,
            XTArtifactTemplate.class,
            XTPolicyType.class,
            XTPolicyTemplate.class
    };
}
