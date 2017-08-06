/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 */
import { SelectData } from '../../../wineryInterfaces/selectData';

export class ValidEndingsData {
    validSource: ValidEndingsApiDataSet = new ValidEndingsApiDataSet();
    validTarget: ValidEndingsApiDataSet = new ValidEndingsApiDataSet();
}

export class ValidEndingsApiDataSet {
    validEndingsSelectionType: ValidEndingsSelectionEnum = ValidEndingsSelectionEnum.EVERYTHING;
    validDataSet: SelectData = new SelectData();
}

export enum ValidEndingsSelectionEnum {
    EVERYTHING = 'EVERYTHING',
    NODETYPE = 'NODETYPE',
    REQTYPE = 'REQUIREMENTTYPE',
    CAPTYPE = 'CAPABILITYTYPE'
}
