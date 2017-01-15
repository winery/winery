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
package org.eclipse.winery.repository.resources;

import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;

public abstract class AbstractComponentInstanceWithReferencesResource extends AbstractComponentInstanceResource {

	protected AbstractComponentInstanceWithReferencesResource(TOSCAComponentId id) {
		super(id);
	}

	/**
	 * Ensures that the presented XML is in line with the stored files
	 */
	@Override
	public Response getXML() {
		this.synchronizeReferences();
		return super.getXML();
	}

	@Override
	public String getDefinitionsAsXMLString() {
		this.synchronizeReferences();
		return super.getDefinitionsAsXMLString();
	}

	@Override
	public Definitions getDefinitions() {
		this.synchronizeReferences();
		return super.getDefinitions();
	}

	/**
	 * Synchronizes the artifact references with the files stored in the
	 * repository
	 */
	protected abstract void synchronizeReferences();

}
