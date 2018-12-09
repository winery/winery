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

/**
 * Encompasses the requirements data defined by the user when using the modal
 */
export class RequirementsModalData {

    constructor(public reqId?: string,
                public oldReqId?: string,
                public reqColor?: string,
                public reqQName?: string,
                public reqQNameLocalName?: string,
                public reqType?: string,
                public reqDefinitionName?: string,
                public requirements?: any,
                public reqDefinitionNames?: Array<string>,
                public nodeId?: string,
                public propertyType?: string,
                public properties?: any) {
    }
}
