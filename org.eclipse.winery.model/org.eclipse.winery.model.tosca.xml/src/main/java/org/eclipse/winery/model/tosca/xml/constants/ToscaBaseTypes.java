/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import javax.xml.namespace.QName;

public class ToscaBaseTypes {
    
    public static final QName hostedOnRelationshipType = QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn");
    public static final QName connectsToRelationshipType = QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ConnectsTo");
    public static final QName dependsOnRelationshipType = QName.valueOf("{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}DependsOn");
}
