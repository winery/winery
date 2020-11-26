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
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import org.eclipse.jdt.annotation.Nullable;

public enum YTStatusValue {
    supported,
    unsupported,
    experimental,
    deprecated;

    @Nullable
    public static YTStatusValue getStatus(String status) {
        switch (status) {
            case "supported":
                return YTStatusValue.supported;
            case "unsupported":
                return YTStatusValue.unsupported;
            case "experimental":
                return YTStatusValue.experimental;
            case "deprecated":
                return YTStatusValue.deprecated;
            default:
                return null;
        }
    }
}
