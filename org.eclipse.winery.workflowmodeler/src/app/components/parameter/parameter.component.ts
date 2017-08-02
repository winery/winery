/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */

import { Component, Input } from '@angular/core';
import { Parameter } from '../../model/parameter';

/**
 * this component contains in property component if the corresponding node has parameter properties
 * eg. task node have input and output params, start event node has input param
 */
@Component({
    selector: 'b4t-parameter',
    styleUrls: ['./parameter.component.css'],
    templateUrl: 'parameter.component.html',
})
export class WmParameterComponent {
    @Input() public param: Parameter;
}
