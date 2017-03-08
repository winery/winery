/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({

    selector: 'winery-table',
    templateUrl: 'table.component.html',
    styleUrls: ['table.component.css']
})
export class TableComponent implements OnInit {

    public page: number = 1;
    @Input() itemsPerPage: number = 10;
    @Input() maxSize: number = 5;
    @Input() numPages: number = 1;
    @Input() length: number = 0;
    @Input() disableFiltering: boolean = false;

    public rows: Array<any> = [];

    @Input() columns: Array<any>;
    @Input() filterString: string;

    @Input() config: any = {
        paging: true,
        sorting: {columns: this.columns || true},
        filtering: {filterString: ''},
        className: ['table-striped', 'table-bordered']
    };

    @Input() data: Array<any> = [];
    currentSelected: any = null;

    @Output() cellSelected = new EventEmitter <any>();
    @Output() removeBtnClicked = new EventEmitter <any>();
    @Output() addBtnClicked = new EventEmitter <any>();

    private oldData: Array<any> = this.data;
    private oldLength = this.oldData.length;

    // region #######Table events and functions######

    public onChangeTable(config: any, page: any = {page: this.page, itemsPerPage: this.itemsPerPage}): any {
        if (config.filtering) {
            Object.assign(this.config.filtering, config.filtering);
        }

        if (config.sorting) {
            Object.assign(this.config.sorting, config.sorting);
        }

        let filteredData = this.changeFilter(this.data, this.config);
        let sortedData = this.changeSort(filteredData, this.config);
        this.rows = page && config.paging ? this.changePage(page, sortedData) : sortedData;
        this.length = sortedData.length;
    }

    public changePage(page: any, data: Array<any> = this.data): Array<any> {
        let start = (page.page - 1) * page.itemsPerPage;
        let end = page.itemsPerPage > -1 ? (start + page.itemsPerPage) : data.length;
        return data.slice(start, end);
    }

    public changeSort(data: any, config: any): any {
        if (!config.sorting) {
            return data;
        }
        // console.log('changeSort:config', config);
        let columns = this.config.sorting.columns || [];
        let columnName: string = void 0;
        let sort: string = void 0;

        for (let i = 0; i < columns.length; i++) {
            if (columns[i].sort !== '' && columns[i].sort !== false) {
                columnName = columns[i].name;
                sort = columns[i].sort;
            }
        }

        if (!columnName) {
            return data;
        }

        // simple sorting
        return data.sort((previous: any, current: any) => {
            if (previous[columnName] > current[columnName]) {
                return sort === 'desc' ? -1 : 1;
            } else if (previous[columnName] < current[columnName]) {
                return sort === 'asc' ? -1 : 1;
            }
            return 0;
        });
    }

    public changeFilter(data: any, config: any): any {
        let filteredData: Array<any> = data;
        this.columns.forEach((column: any) => {
            if (column.filtering) {
                filteredData = filteredData.filter((item: any) => {
                    return item[column.name].match(column.filtering.filterString);
                });
            }
        });

        if (!config.filtering) {
            return filteredData;
        }

        if (config.filtering.columnName) {
            return filteredData.filter((item: any) =>
                item[config.filtering.columnName].match(this.config.filtering.filterString));
        }

        let tempArray: Array<any> = [];
        filteredData.forEach((item: any) => {
            let flag = false;
            this.columns.forEach((column: any) => {
                if (item[column.name].toString().match(this.config.filtering.filterString)) {
                    flag = true;
                }
            });
            if (flag) {
                tempArray.push(item);
            }
        });
        filteredData = tempArray;

        return filteredData;
    }

    onCellClick(data: any) {
        this.cellSelected.emit(data);
        this.currentSelected = data.row;
    }

    onAddClick($event: Event) {
        $event.stopPropagation();
        this.addBtnClicked.emit();
    }

    onRemoveClick($event: Event) {
        $event.stopPropagation();
        this.removeBtnClicked.emit(this.currentSelected);
    }

    onItemsPerPageChange(event: Event, selectElement: any) {
        event.stopPropagation();
        this.itemsPerPage = selectElement.value;
        this.onChangeTable(this.config);
    }

    constructor() {
        // this.length = this.data.length;
    }

    ngOnInit() {
        this.config.sorting.columns = this.columns;
        this.length = this.data.length;
        this.onChangeTable(this.config);
    }

    // We "know" that the only way the list can change is
    // identity or in length so that's all we check
    ngDoCheck() {
        if (this.oldData !== this.data) {
            this.oldData = this.data;
            this.oldLength = this.data.length;
            this.onChangeTable(this.config);
        } else {
            let newLength = this.data.length;
            let old = this.oldLength;
            if (old !== newLength) {
                // let direction = old < newLength ? 'grew' : 'shrunk';
                // this.logs.push(`heroes ${direction} from ${old} to ${newLength}`);
                this.oldLength = newLength;
                this.onChangeTable(this.config);
            }
        }
    }

}
