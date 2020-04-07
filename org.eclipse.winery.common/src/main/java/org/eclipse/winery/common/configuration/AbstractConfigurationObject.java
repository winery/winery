/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.configuration;

import org.apache.commons.configuration2.YAMLConfiguration;

public abstract class AbstractConfigurationObject {
    //Holds a pointer to the configuration used for saving.
    protected YAMLConfiguration configuration;

    /**
     * Saves changes to the configuration object
     */
    abstract void save();

    /**
     * Updates the configuration object instance when the configuration is changed
     */
    abstract void update(YAMLConfiguration updatedConfiguration);

    /**
     * Does necessary set up for the corresponding part of configuration. Should be called in the constructor.
     */
    abstract void initialize();
}
