/********************************************************************************
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

package org.eclipse.winery.repository.rest.resources._support;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public abstract class AbstractComponentInstanceResourceContainingATopology extends AbstractComponentInstanceWithReferencesResource {

    protected AbstractComponentInstanceResourceContainingATopology(DefinitionsChildId id) {
        super(id);
    }

    public abstract void setTopology(TTopologyTemplate topologyTemplate, String type);
}
