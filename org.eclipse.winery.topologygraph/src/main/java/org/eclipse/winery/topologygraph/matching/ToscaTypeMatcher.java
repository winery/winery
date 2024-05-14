/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaNode;

public class ToscaTypeMatcher implements IToscaMatcher {

    private final boolean ignoreVersions;

    public ToscaTypeMatcher() {
        this(false);
    }

    public ToscaTypeMatcher(boolean ignoreVersion) {
        this.ignoreVersions = ignoreVersion;
    }

    @Override
    public boolean isCompatible(ToscaNode left, ToscaNode right) {
        return right.getNodeTypes().stream()
            .anyMatch(type -> typesCompatible(left.getActualType().getQName(), type.getQName()));
    }

    @Override
    public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
        return right.getRelationshipTypes().stream()
            .anyMatch(type -> typesCompatible(left.getActualType().getQName(), type.getQName()));
    }
    
    private boolean typesCompatible(QName leftQName, QName rightQName) {
        if (ignoreVersions) {
            return leftQName.getNamespaceURI().equals(rightQName.getNamespaceURI()) &&
                VersionUtils.getNameWithoutVersion(leftQName.getLocalPart()).equals(
                    VersionUtils.getNameWithoutVersion(rightQName.getLocalPart())
                );
        }
        return leftQName.equals(rightQName);
    }
}
