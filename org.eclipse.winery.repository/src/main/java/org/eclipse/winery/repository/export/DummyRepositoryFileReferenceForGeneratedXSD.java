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
