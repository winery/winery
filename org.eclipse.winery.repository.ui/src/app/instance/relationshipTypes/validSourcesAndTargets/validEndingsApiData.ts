/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import {SelectData} from '../../../wineryInterfaces/selectData';

export enum ValidEndingsSelectionEnum {
    EVERYTHING = 'EVERYTHING',
    NODETYPE = 'NODETYPE',
    REQTYPE = 'REQUIREMENTTYPE',
    CAPTYPE = 'CAPABILITYTYPE'
}

export class ValidEndingsApiDataSet {
    validEndingsSelectionType: ValidEndingsSelectionEnum = ValidEndingsSelectionEnum.EVERYTHING;
    validDataSet: SelectData = new SelectData();
}

export class ValidEndingsData {
    validSource: ValidEndingsApiDataSet = new ValidEndingsApiDataSet();
    validTarget: ValidEndingsApiDataSet = new ValidEndingsApiDataSet();
}
