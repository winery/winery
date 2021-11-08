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

package org.eclipse.winery.model.adaptation.problemsolving.algorithms;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;

public class SecureVmProxyAlgorithm extends AbstractSecureProxyAlgorithm {

    @Override
    protected TNodeTemplate createProxy(TNodeTemplate sourceNode) {
        TNodeTemplate proxy = new TNodeTemplate();
        proxy.setType(OpenToscaBaseTypes.secureProxy);
        proxy.setName(OpenToscaBaseTypes.secureProxy.getLocalPart());
        proxy.setId(sourceNode.getId() + "_proxy");
        return proxy;
    }

    @Override
    protected QName getHostComponentType() {
        return OpenToscaBaseTypes.virtualMachineNodeType;
    }
}
