/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import org.eclipse.winery.common.RepositoryFileReference;

import org.w3c.dom.Document;

/**
 * Class used to indicate reference to a generated XSD
 */
public class DummyRepositoryFileReferenceForGeneratedXSD extends RepositoryFileReference {

	private final Document document;


	/**
	 * @param document the W3C DOM Document holding the generated XSD
	 */
	public DummyRepositoryFileReferenceForGeneratedXSD(Document document) {
		// we have to create a unique filename in the case two different XSDs are exported
		// document.hashCode should be unique enough for us
		super(new DummyParentForGeneratedXSDRef(), Integer.toString(document.hashCode()));
		this.document = document;
	}

	public Document getDocument() {
		return this.document;
	}
}
