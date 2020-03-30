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
package org.eclipse.winery.repository.rest.resources._support.collections.withoutid;

import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;

public class IdDeterminationWithHashCode implements IIdDetermination<Object> {

    public static final IdDeterminationWithHashCode INSTANCE = new IdDeterminationWithHashCode();

    @Override
    public String getId(Object entity) {
        // We assume that different Object serializations *always* have different hashCodes
//        int hash = BackendUtils.getXMLAsString(entity, repository).hashCode();
        // assume that an entity knows how to hashCode itself
        int hash = entity.hashCode();
        return Integer.toString(hash);
    }

    /**
     * Static wrapper method for functions.tld
     */
    public static String getIdStatically(Object entity) {
        return IdDeterminationWithHashCode.INSTANCE.getId(entity);
    }

}
