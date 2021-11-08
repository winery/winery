/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;

/**
 * Holds the state of ids regarding the export.
 * <p>
 * Required as we do not know at the entry point (usually a service template), which other components are linked.
 * <p>
 * Users can call flagAsExportRequired more than once for the same id. If an id is already exported, it is not flagged
 * as exported again.
 */
public class ExportedState {

    private final Collection<DefinitionsChildId> exported = new HashSet<>();
    private final Queue<DefinitionsChildId> exportRequired = new ArrayDeque<>();

    /**
     * Get the first definition child id to be exported, null if no more elements are in the queue.
     *
     * @return DefinitionsChildId
     */
    public DefinitionsChildId pop() {
        return this.exportRequired.poll();
    }

    public void flagAsExported(DefinitionsChildId id) {
        this.exportRequired.remove(id);
        this.exported.add(id);
    }

    /**
     * Flags the given id as required for export, if not already exported
     *
     * @param id the id to flag
     */
    public void flagAsExportRequired(DefinitionsChildId id) {
        if (!this.exported.contains(id)) {
            this.exportRequired.add(id);
        }
    }

    public void flagAsExportRequired(Collection<DefinitionsChildId> ids) {
        for (DefinitionsChildId id : ids) {
            if ((!this.exported.contains(id)) && (!this.exportRequired.contains(id))) {
                this.exportRequired.add(id);
            }
        }
    }
}
