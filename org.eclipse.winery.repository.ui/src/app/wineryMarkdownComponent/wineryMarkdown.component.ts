/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { MarkdownService } from 'angular2-markdown';

@Component({
    selector: 'winery-markdown',
    encapsulation: ViewEncapsulation.None,
    templateUrl: './wineryMarkdown.component.html',
    providers: [],
    styleUrls: ['wineryMarkdown.component.css'],

})
export class WineryMarkdownComponent implements OnInit {

    @Input() markdownContent = '';

    constructor(private _markdown: MarkdownService) {
    }

    ngOnInit() {
        this._markdown.setMarkedOptions({});
        this._markdown.setMarkedOptions({
            gfm: true,
            tables: true,
            breaks: false,
            pedantic: false,
            sanitize: false,
            smartLists: true,
            smartypants: false
        });

        this._markdown.renderer.table = (header: string, body: string) => {
            return `
        <table class="table2">
          <thead>
            ${header}
          </thead>
          <tbody>
            ${body}
          </tbody>
        </table>
        `;
        };
        this._markdown.renderer.blockquote = (quote: string) => {
            return `<blockquote class="king-quote">${quote}</blockquote>`;
        };
    }

}
