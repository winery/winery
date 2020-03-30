/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.datatypes.ids.elements;

import org.eclipse.winery.model.ids.GenericId;

public class GenericDirectoryId extends DirectoryId {
    public GenericDirectoryId(GenericId parent, String directoryId) {
        super(parent, directoryId);
    }

    public GenericDirectoryId(GenericId parent, String directoryId, boolean URLencoded) {
        super(parent, directoryId, URLencoded);
    }
}
