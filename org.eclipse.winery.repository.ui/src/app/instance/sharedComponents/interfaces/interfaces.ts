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
export enum Interfaces {

    LIFECYCLE_STANDARD = 'http://opentosca.org/interfaces/lifecycle',
    LIFECYCLE_STANDARD_INSTALL = 'install',
    LIFECYCLE_STANDARD_CONFIGURE = 'configure',
    LIFECYCLE_STANDARD_START = 'start',
    LIFECYCLE_STANDARD_STOP = 'stop',
    LIFECYCLE_STANDARD_UNINSTALL = 'uninstall',

    RELATIONSHIP_CONFIGURE = 'http://docs.oasis-open.org/tosca/ns/2011/12/interfaces/relationship/configure',
    RELATIONSHIP_CONFIGURE_PRE_CONFIGURE_SOURCE = 'preConfigureSource',
    RELATIONSHIP_CONFIGURE_PRE_CONFIGURE_TARGET = 'preConfigureTarget',
    RELATIONSHIP_CONFIGURE_POST_CONFIGURE_SOURCE = 'postConfigureSource',
    RELATIONSHIP_CONFIGURE_POST_CONFIGURE_TARGET = 'postConfigureTarget'
}
