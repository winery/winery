/*******************************************************************************
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
package org.eclipse.winery.model.threatmodeling;

import io.github.adr.embedded.ADR;

/**
 * Hardcoded namespaces, NodeTypes and PolicyTypes for threat modeling capabilities
 */
final class ThreatModelingConstants {
    @ADR(28)
    public static final String THREATMODELING_NAMESPACE = "http://opentosca.org/threatmodeling";
    public static final String SECURITY_NAMESPACE = "http://opentosca.org/nfv/security";

    public static final String THREAT_POLICY_NAME = "Security.Threat_w1-wip1";
    public static final String THREAT_POLICY_ID = String.format("{%s}%s", THREATMODELING_NAMESPACE, THREAT_POLICY_NAME);

    public static final String MITIGATION_POLICY_NAME = "Security.Mitigation_w1-wip1";
    public static final String MITIGATION_POLICY_ID = String.format("{%s}%s", THREATMODELING_NAMESPACE, MITIGATION_POLICY_NAME);

    public static final String SVNF_NODE_TYPE = String.format("{%s}S-VNF_w1-wip1", SECURITY_NAMESPACE);
}
