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

    public static final QName OperatingSystem = QName.valueOf("{http://opentosca.org/nodetypes}OperatingSystem");
    public static final QName Ubuntu18NodeType = QName.valueOf("{http://opentosca.org/nodetypes}Ubuntu-VM_18.04-w1");
    
    public static final QName ubuntuNodeTypeImpl = QName.valueOf("{http://opentosca.org/nodetypeimplementations}Ubuntu-VM");
    public static final QName ubuntuNodeType = QName.valueOf("{http://opentosca.org/nodetypes}Ubuntu-VM");
    public static final QName dockerContainerNodeType = QName.valueOf("{http://opentosca.org/nodetypes}DockerContainer");
    public static final QName dockerContainerArtifactType = QName.valueOf("{http://opentosca.org/artifacttypes}DockerContainerArtifact");

    // region ********** secure elements **********
    public static final QName secureProxyContainer = QName.valueOf("{http://opentosca.org/secureelements/nodetypes}SecureProxyContainer");
    public static final QName secureProxy = QName.valueOf("{http://opentosca.org/secureelements/nodetypes}SecureProxy");
    // endregion
    
    //region ********** messaging elements **********
    //TODO: Check if these Types are correct for final version
    public static final QName publisherProxy = QName.valueOf("{http://opentosca.org/patternsolutions/nodetypes}PublisherProxy");
    public static final QName subscriberProxy = QName.valueOf("{http://opentosca.org/patternsolutions/nodetypes}SubscriberProxy");
    public static final QName topic = QName.valueOf("{http://opentosca.org/nodetypes}Topic");
    public static final QName topicReqType = QName.valueOf("{http://opentosca.org/requirementtypes}MessageBroker");
    public static final QName proxyReqType = QName.valueOf("{http://opentosca.org/requirementtypes}Java11Runtime");
    public static final QName abstractJava11DriverTemplate = QName.valueOf("{http://opentosca.org/driverinjection/artifacttemplates}Abstract-Java11Driver");
    public static final QName topicConnectsTo = QName.valueOf("{http://opentosca.org/driverinjection/relationshiptypes}ConnectsToTopic");
    //endregion
    
    //region ********** multiparticipant-placeholder **********
    public static final String placeholderTypeNamespace = "http://opentosca/multiparticipant/placeholdertypes";
    //endregion

    // region ********** freeze and defrost **********
    public static final QName stateArtifactType = QName.valueOf("{http://opentosca.org/artifacttypes}State");
    public static final QName isoArtifactType = QName.valueOf("{http://opentosca.org/artifacttypes}ISO");
    public static final QName cloudImageArtifactType = QName.valueOf("{http://opentosca.org/artifacttypes}CloudImage");
    public static final QName statefulComponentPolicyType = QName.valueOf("{http://opentosca.org/policytypes}StatefulComponent");
    public static final QName freezableComponentPolicyType = QName.valueOf("{http://opentosca.org/policytypes}FreezableComponent");
    // endregion

    // region ********** management features **********
    public static final QName managementFeatureRequirement = QName.valueOf("{http://opentosca.org/management/features/requirementtypes}FeatureRequirement");
    // endregion
    
    public static final String namespaceBase = "http://opentosca.org/";
    public static final String artifactTemplateNamespace = namespaceBase + "artifacttemplates";
}
