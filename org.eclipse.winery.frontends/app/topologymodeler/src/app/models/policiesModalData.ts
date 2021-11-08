/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
 * Encompasses the policies data defined by the user when using the modal
 */
export class PoliciesModalData {

    constructor(public policyTemplates?: string,
                public policyTemplate?: string,
                public policyTemplateColor?: string,
                public policyTemplateId?: string,
                public policyTemplateName?: string,
                public policyTemplateNamespace?: string,
                public policyTemplateQName?: string,
                public policyTypes?: string,
                public policyType?: string,
                public policyTypeColor?: string,
                public policyTypeId?: string,
                public policyTypeName?: string,
                public policyTypeNamespace?: string,
                public policyTypeQName?: string,
                public policies?: any,
                public nodeTemplateId?: string) {
    }
}

export class TPolicy {
    constructor(public name?: string,
                public policyRef?: string,
                public policyType?: string,
                public any?: any[],
                public documentation?: any[],
                public otherAttributes?: any,
                public properties?: any,
                public targets?: string[]) {
    }
}
