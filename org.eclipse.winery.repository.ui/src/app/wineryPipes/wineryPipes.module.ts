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
import { NgModule } from '@angular/core';
import { UrlEncodePipe } from './urlEncode.pipe';
import { UrlDecodePipe } from './urlDecode.pipe';
import { RemoveWhiteSpacesPipe } from './removeWhiteSpaces.pipe';
import { ToscaTypeToCamelCase } from './toscaTypeToCamelCase.pipe';
import { ToscaTypeToReadableNamePipe } from './toscaTypeToReadableName.pipe';

@NgModule({
    imports: [],
    exports: [
        ToscaTypeToCamelCase,
        ToscaTypeToReadableNamePipe,
        RemoveWhiteSpacesPipe,
        UrlDecodePipe,
        UrlEncodePipe
    ],
    declarations: [
        ToscaTypeToCamelCase,
        ToscaTypeToReadableNamePipe,
        RemoveWhiteSpacesPipe,
        UrlDecodePipe,
        UrlEncodePipe,
    ],
    providers: [],
})
export class WineryPipesModule {
}
