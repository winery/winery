/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.datatypes.ids.elements;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;

/**
 * Id of the directory containing the self-service metadata
 */
public class SelfServiceMetaDataId extends ToscaElementId {

    public SelfServiceMetaDataId(ServiceTemplateId parent) {
        super(parent, new XmlId(Constants.DIRNAME_SELF_SERVICE_METADATA, true));
    }
}
