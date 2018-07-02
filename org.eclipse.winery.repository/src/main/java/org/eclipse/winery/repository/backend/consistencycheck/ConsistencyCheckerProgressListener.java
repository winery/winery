/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.repository.backend.consistencycheck;

/**
 * This interface is used by the consistency checker to output updates on the status. Implementations may log something our output something to the console.
 */
public interface ConsistencyCheckerProgressListener {

    /**
     * Accepts the progress in the range of [0-1] to publish the current checking progress to the caller.
     *
     * @param progress The current progress between 0 and 1
     */
    default void updateProgress(float progress) {
        // don't do anything as the default implementation
    }

    /**
     * Updates the caller with detailed information about the consistency check. This method is only called, if the
     * {@link ConsistencyCheckerConfiguration}'s verbosity set contains the OUTPUT_CURRENT_TOSCA_COMPONENT_ID key.
     *
     * @param progress           The current progress between 0 and 1
     * @param checkingDefinition The readable definition's id currently under investigation
     */
    default void updateProgress(float progress, String checkingDefinition) {
        // don't do anything as the default implementation
    }
}
