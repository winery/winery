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
package org.eclipse.winery.repository.resources._support.collections.withoutid;

import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.resources._support.collections.IIdDetermination;

public class IdDeterminationWithHashCode implements IIdDetermination<Object> {

	public static final IdDeterminationWithHashCode INSTANCE = new IdDeterminationWithHashCode();

	@Override
	public String getId(Object entity) {
		// We assume that different Object serializations *always* have different hashCodes
		int hash = Utils.getXMLAsString(entity).hashCode();
		return Integer.toString(hash);
	}

	/**
	 * Static wrapper method for functions.tld
	 */
	public static String getIdStatically(Object entity) {
		return IdDeterminationWithHashCode.INSTANCE.getId(entity);
	}

}
