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
import {
    AfterViewInit, Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { LiveModelingLog } from '../../models/liveModelingLog';
import { Subscription } from 'rxjs';
import { LoggingService } from '../../services/logging.service';
import { LiveModelingLogTypes } from '../../models/enums';

@Component({
    selector: 'winery-live-modeling-sidebar-logs',
    templateUrl: './logs.component.html',
    styleUrls: ['./logs.component.css'],
})
export class LogsComponent implements OnInit, OnDestroy, AfterViewInit {

    readonly SCROLL_THRESHOLD = 50;
    isNearBottom = true;
    logs: Array<LiveModelingLog> = [];

    subscriptions: Array<Subscription> = [];

    @ViewChild('scrollContainer') private scrollContainer: ElementRef;
    @ViewChildren('logItems') private logItems: QueryList<any>;

    constructor(private loggingService: LoggingService) {
    }

    ngOnInit() {
        this.subscriptions.push(this.loggingService.logStream.subscribe((logs) => {
            this.logs = logs;
        }));
    }

    ngAfterViewInit(): void {
        this.subscriptions.push(this.logItems.changes.subscribe((_) => {
            this.scrollToBottom();
        }));
    }

    getBadgeBackgroundForLog(type: LiveModelingLogTypes) {
        switch (type) {
            case LiveModelingLogTypes.INFO:
                return '#007bff';
            case LiveModelingLogTypes.SUCCESS:
                return '#28a745';
            case LiveModelingLogTypes.WARNING:
                return '#ffc107';
            case LiveModelingLogTypes.DANGER:
                return '#dc3545';
            case LiveModelingLogTypes.CONTAINER:
                return '#6c757d';
        }
    }

    scrolled() {
        const position = this.scrollContainer.nativeElement.scrollTop + this.scrollContainer.nativeElement.offsetHeight;
        const height = this.scrollContainer.nativeElement.scrollHeight;
        this.isNearBottom = position > height - this.SCROLL_THRESHOLD;
    }

    clearLogs() {
        this.loggingService.clearLogs();
    }

    ngOnDestroy() {
        this.subscriptions.forEach((subscription) => {
            subscription.unsubscribe();
        });
    }

    private scrollToBottom(): void {
        if (this.isNearBottom) {
            this.scrollContainer.nativeElement.scroll({
                top: this.scrollContainer.nativeElement.scrollHeight,
                left: 0,
                behavior: 'smooth'
            });
        }
    }
}
