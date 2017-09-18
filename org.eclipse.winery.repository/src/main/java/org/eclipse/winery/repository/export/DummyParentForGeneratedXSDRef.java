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

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.XMLId;

public class DummyParentForGeneratedXSDRef extends GenericId {

	protected DummyParentForGeneratedXSDRef() {
		super(new XMLId("dummy", false));
	}

	@Override
	public int compareTo(GenericId o) {
		throw new IllegalStateException("Should never be called.");
	}

	@Override
	public GenericId getParent() {
		throw new IllegalStateException("Should never be called.");
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof DummyParentForGeneratedXSDRef);
	}

	@Override
	public int hashCode() {
		return 0;
	}

}
