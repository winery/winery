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
import {Component, OnInit, ViewChild} from '@angular/core';
import {GitLogApiData} from './GitLogApiData';
import {WineryNotificationService} from '../wineryNotificationModule/wineryNotification.service';
import {ModalDirective} from 'ngx-bootstrap';
import {Router} from '@angular/router';
import {webSocketURL} from '../configuration';

@Component({
    selector: 'winery-gitlog',
    templateUrl: 'wineryGitLog.component.html',
    styleUrls: [
        'wineryGitLog.component.css'
    ]
})
export class WineryGitLogComponent implements OnInit {

    webSocket: WebSocket;
    isExpanded = false;
    lfsAvailable = false;
    files: GitLogApiData[] = [];
    selectedFile: GitLogApiData;
    commitMsg = '';
    show = false;

    @ViewChild('confirmDiscardModal') confirmDiscardModal: ModalDirective;

    constructor(private notify: WineryNotificationService,
                private router: Router) {
    }

    ngOnInit() {
        this.webSocket = new WebSocket(webSocketURL + '/git');
        this.webSocket.onopen = event => {
            this.refreshLog();
            this.show = true;
        };

        this.webSocket.onmessage = event => {
            switch (event.data) {
                case 'commit success': {
                    this.notify.success('Commited: ' + this.commitMsg);
                    this.commitMsg = '';
                    this.selectedFile = null;
                    break;
                }
                case 'commit failed': {
                    this.notify.error('commit failed');
                    break;
                }
                case 'reset failed': {
                    this.notify.error('winery-repository reset to last commit failed!');
                    break;
                }
                case 'reset success': {
                    this.notify.success('winery-repository resetted to last commit');
                    this.router.navigate(['/']);
                    break;
                }
                case 'git-lfs': {
                    this.lfsAvailable = true;
                    break;
                }
                default: {
                    this.files = JSON.parse(event.data);
                    for (let i = 0; i < this.files.length; i++) {
                        this.files[i].name = decodeURIComponent(decodeURIComponent(this.files[i].name));
                    }
                }
            }

        };

        this.webSocket.onclose = event => {
            this.webSocket.close();
        };
    }

    commit() {
        if (this.files === null || this.files.length === 0) {
            this.notify.error('A commit must contain at least one change!');
        } else if (this.commitMsg === '') {
            this.notify.error('Please enter a valid commit message!');
        } else {
            this.webSocket.send(this.commitMsg);
        }
    }

    refreshLog() {
        this.webSocket.send('');
    }

    doCommitMsgValueChange(data: any) {
        this.commitMsg = data.target.value;
    }

    select(file: GitLogApiData) {
        if (this.selectedFile === file) {
            this.selectedFile = null;
        } else {
            this.selectedFile = file;
        }
    }

    onExpand() {
        this.isExpanded = !this.isExpanded;
        this.selectedFile = null;
    }

    discardChanges() {
        this.webSocket.send('reset');
        this.hide();
    }

    hide() {
        this.selectedFile = null;
        this.isExpanded = false;
    }
}
