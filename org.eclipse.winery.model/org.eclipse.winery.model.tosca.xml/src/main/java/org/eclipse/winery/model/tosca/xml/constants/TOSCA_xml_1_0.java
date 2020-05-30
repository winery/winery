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

import org.eclipse.winery.model.tosca.xml.TArtifactTemplate;
import org.eclipse.winery.model.tosca.xml.TArtifactType;
import org.eclipse.winery.model.tosca.xml.TCapabilityType;
import org.eclipse.winery.model.tosca.xml.TExtensibleElements;
import org.eclipse.winery.model.tosca.xml.TNodeType;
import org.eclipse.winery.model.tosca.xml.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.xml.TPolicyTemplate;
import org.eclipse.winery.model.tosca.xml.TPolicyType;
import org.eclipse.winery.model.tosca.xml.TRelationshipType;
import org.eclipse.winery.model.tosca.xml.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.xml.TRequirementType;
import org.eclipse.winery.model.tosca.xml.TServiceTemplate;

public class TOSCA_xml_1_0 {
    @SuppressWarnings("unchecked")
    public static final Class<? extends TExtensibleElements>[] DEFINITIONS_ELEMENT_CLASSES =
        new Class[] {
            TServiceTemplate.class,
            TNodeType.class,
            TNodeTypeImplementation.class,
            TRelationshipType.class,
            TRelationshipTypeImplementation.class,
            TRequirementType.class,
            TCapabilityType.class,
            TArtifactType.class,
            TArtifactTemplate.class,
            TPolicyType.class,
            TPolicyTemplate.class
    };
}
