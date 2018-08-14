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

import { Inject, Injectable } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';

/**
 * Fires event if app is loaded.
 */
@Injectable()
export class AppReadyEventService {

    private doc: Document;
    private isAppReady: boolean;

    constructor(@Inject(DOCUMENT) doc: any) {
        this.doc = doc;
        this.isAppReady = false;
    }

    /**
     * Fires event if the app has done loading
     */
    public trigger(): void {
        if (this.isAppReady) {
            return;
        }

        const bubbles = true;
        const cancelable = false;

        this.doc.dispatchEvent(this.createEvent('appready', bubbles, cancelable));
        this.isAppReady = true;
    }

    /**
     * Creates a custom event.
     * @param eventType
     * @param bubbles
     * @param cancelable
     */
    private createEvent(eventType: string, bubbles: boolean, cancelable: boolean): Event {
        const customEvent: any = new CustomEvent(eventType, {bubbles: bubbles, cancelable: cancelable});
        return customEvent;
    }
}
