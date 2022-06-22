/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import { Injectable } from '@angular/core';
import { OverlayComponent } from '../overlay/overlay.component';

@Injectable()
export class OverlayService {
    private overlay: OverlayComponent;

    constructor() {
    }

    public initOverlayService(overlay: OverlayComponent) {
        this.overlay = overlay;
    }

    public showOverlay(content: string) {
        this.overlay.content = content;
        this.overlay.visible = true;
    }

    public hideOverlay() {
        this.overlay.visible = false;
    }
}
