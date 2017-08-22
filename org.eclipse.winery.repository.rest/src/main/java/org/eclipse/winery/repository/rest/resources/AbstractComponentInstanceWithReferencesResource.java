/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.Definitions;

public abstract class AbstractComponentInstanceWithReferencesResource extends AbstractComponentInstanceResource {

	protected AbstractComponentInstanceWithReferencesResource(TOSCAComponentId id) {
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
