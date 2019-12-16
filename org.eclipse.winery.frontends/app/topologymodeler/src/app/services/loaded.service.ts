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

// Import the core angular services.
import { Injectable } from '@angular/core';

// Import the rxJs modules for their side-effects.

export interface ILoaded {
    loadedData: boolean;
    generatedReduxState: boolean;
}

@Injectable()
export class LoadedService {

    constructor() {
    }
}
