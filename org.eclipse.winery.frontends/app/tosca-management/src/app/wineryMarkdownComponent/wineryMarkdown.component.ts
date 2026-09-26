/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, Input, ViewEncapsulation } from '@angular/core';
import { marked } from 'marked';

const renderer = new marked.Renderer();
renderer.table = (header: string, body: string) => `
        <table class="table2">
          <thead>
            ${header}
          </thead>
          <tbody>
            ${body}
          </tbody>
        </table>
        `;
renderer.blockquote = (quote: string) => `<blockquote class="king-quote">${quote}</blockquote>`;

@Component({
    selector: 'winery-markdown',
    encapsulation: ViewEncapsulation.None,
    templateUrl: './wineryMarkdown.component.html',
    providers: [],
    styleUrls: ['wineryMarkdown.component.css'],

})
export class WineryMarkdownComponent {

    @Input() markdownContent = '';

    get html(): string {
        return marked.parse(this.markdownContent || '', { gfm: true, breaks: false, renderer });
    }

}
