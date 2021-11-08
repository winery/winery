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

import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;

public class IdDeterminationWithHashCode implements IIdDetermination<Object> {

    private final IRepository repository;

    public IdDeterminationWithHashCode(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getId(Object entity) {
        // We assume that different Object serializations *always* have different hashCodes
        // This id generation strategy matches the one employed for searching through a list of entities
        // for EntityWithoutIdCollectionResource
        // notably this also assumes that the serialization of an entity is deterministic if it does not have an id
        int hash = BackendUtils.getXMLAsString(entity, repository).hashCode();
        return Integer.toString(hash);
    }
}
