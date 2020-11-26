/********************************************************************************
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

import { QName } from '../../../../shared/src/app/model/qName';

/**
 * Enrichment interface containing features of FeatureEntity type and a node template id.
 */
export interface Enrichment {
    features: FeatureEntity[];
    nodeTemplateId: string;
    length: number;
}

/**
 * FeatureEntity interface containing type of the feature and the feature name.
 */
export interface FeatureEntity {
    type: QName;
    featureName: string;
}
