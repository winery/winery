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

package org.eclipse.winery.model.tosca.constants;

import javax.xml.namespace.QName;

public class OpenToscaBaseTypes {

    // region ********** base elements **********
    public static final QName virtualMachineNodeType = QName.valueOf("{http://opentosca.org/baseelements/nodetypes}VM");
    public static final QName DockerEngineNodeType = QName.valueOf("{http://opentosca.org/baseelements/nodetypes}DockerEngine");
    // endregion

    // region ********** secure elements **********
    public static final QName secureProxyContainer = QName.valueOf("{http://opentosca.org/secureelements/nodetypes}SecureProxyContainer");
    public static final QName secureProxy = QName.valueOf("{http://opentosca.org/secureelements/nodetypes}SecureProxy");
    // endregion

    // region ********** freeze and defrost **********
    public static final QName stateArtifactType = QName.valueOf("{http://opentosca.org/artifacttypes}State");
    public static final QName statefulComponentPolicyType = QName.valueOf("{http://opentosca.org/policytypes}StatefulComponent");
    public static final QName freezableComponentPolicyType = QName.valueOf("{http://opentosca.org/policytypes}FreezableComponent");
    // endregion

    // region ********** management features **********
    public static final QName managementFeatureRequirement = QName.valueOf("{http://opentosca.org/management/features/requirementtypes}FeatureRequirement");
    // endregion
}
