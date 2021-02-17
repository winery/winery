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

import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.EntityResource;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;

import java.util.List;

/**
 * {@inheritDoc}
 */
public abstract class EntityWithoutIdResource<EntityT> extends EntityResource<EntityT> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public EntityWithoutIdResource(EntityT o, int idx, List<EntityT> list, IPersistable res) {
        super((IIdDetermination<EntityT>) new IdDeterminationWithHashCode(RepositoryFactory.getRepository()), o, idx, list, res);
    }

}
