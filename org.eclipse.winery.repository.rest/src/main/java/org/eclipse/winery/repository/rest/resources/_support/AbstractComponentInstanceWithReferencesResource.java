/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.Definitions;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public abstract class AbstractComponentInstanceWithReferencesResource extends AbstractComponentInstanceResource {

    protected AbstractComponentInstanceWithReferencesResource(DefinitionsChildId id) {
        super(id);
    }

    @Override
    public String getDefinitionsAsXMLString() {
        try {
            this.synchronizeReferences();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        return super.getDefinitionsAsXMLString();
    }

    @Override
    public Definitions getDefinitions() {
        try {
            this.synchronizeReferences();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        return super.getDefinitions();
    }

    /**
     * Synchronizes the artifact references with the files stored in the
     * repository
     */
    protected abstract void synchronizeReferences() throws IOException;

}
