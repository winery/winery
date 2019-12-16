/*******************************************************************************
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
 *******************************************************************************/
import { Pipe, PipeTransform } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { SectionData } from './sectionData';

export interface SectionPipeInput {
    /**
     * Defines which namespace should be displayed.
     * It can contain <code>'all'</code>, <code>'{namespace}'</code> or <code>'group'</code>.
     */
    showNamespaces: string;
    /**
     * The string which is used to filter the components.
     */
    filterString: string;
}

/**
 * This pipe filters the given components by the given filter string and namespace.
 * If <code>showNamespaces</code> is set to <code>'group'</code>, the components are grouped by the namespaces.
 */
@Pipe({
    name: 'filterSections'
})
export class SectionPipe implements PipeTransform {
    transform(value: SectionData[], args: SectionPipeInput): SectionData[] {
        if (isNullOrUndefined(value)) {
            return value;
        }

        if (isNullOrUndefined(args.filterString)) {
            args.filterString = '';
        }

        if (isNullOrUndefined(args.showNamespaces) || args.showNamespaces === 'all') {
            return value.filter(
                (item: SectionData) => this.filter(item, args.filterString)
            );
        } else if ((args.showNamespaces.length > 0) && !args.showNamespaces.includes('group')) {
            return value.filter(
                (item: SectionData, index: number, array: SectionData[]) => {
                    return (item.namespace === args.showNamespaces)
                        && this.filter(item, args.filterString);
                }
            );
        } else if (args.showNamespaces === 'group') {
            const distinctNamespaces: SectionData[] = [];

            // Get all namespaces and count their appearance
            for (const item of value) {
                if (isNullOrUndefined(distinctNamespaces[item.namespace])) {
                    const o = new SectionData();
                    o.namespace = item.namespace;
                    o.count = 1;
                    distinctNamespaces[item.namespace] = o;
                    distinctNamespaces.push(o);
                } else {
                    distinctNamespaces[item.namespace].count++;
                }
            }

            // Apply the search filter and return the resulting array
            return distinctNamespaces.filter(
                (item: SectionData) => this.filter(item, args.filterString)
            );
        }
    }

    private filter(item: SectionData, filter: string): boolean {
        let containsId = false;
        let containsNamespace = false;

        if (item.id) {
            containsId = item.id.toLowerCase().includes(filter.toLowerCase());
        }
        containsNamespace = item.namespace.toLowerCase().includes(filter.toLowerCase());

        return containsId || containsNamespace;
    }
}
