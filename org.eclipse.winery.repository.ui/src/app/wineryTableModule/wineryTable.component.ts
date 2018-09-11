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
import { Component, DoCheck, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { isNullOrUndefined } from 'util';

/**
 * This component provides an easy and fast way to use the ng2-table with further modifications
 * for the winery. It already enables the search, pagination and adding items to the table.
 * However, most of the configuration can be passed to this object.
 * <p>
 *     In order to use this component, the {@link WineryTableModule} must be imported in the corresponding
 *     module. Afterwards, it can be included in the template by inserting the `<winery-table></winery-table>` tag.
 *     Inputs, which must be passed to the component in order for the table to work, are <code>columns</code> and
 * <code>data</code>. The <code>columns</code> array must contain objects of type {@link WineryTableColumn}. The
 * <code>data</code> can be an array of any kind of objects.
 * </p>
 *
 * <label>Inputs</label>
 * <ul>
 *     <li><code>title</code> sets the title for the selector.
 *     </li>
 *     <li><code>data</code> the list of elements the table should display. This field is mandatory!
 *     </li>
 *     <li><code>columns</code> a list of {@link WineryTableColumn} objects. Specifies the column names and
 *         their corresponding objects' property in the <code>data</code> array.
 *     </li>
 *     <li><code>config</code> sets the search configuration. Is of type:
 *     ```
 *     {
 *        paging: boolean;
 *        sorting: {
 *          columns: (Array<WineryTableColumn>|boolean)
 *        };
 *        filtering: {
 *          filterString: string
 *        };
 *        className: [string,string];
 *     }
 *     ```
 *     </li>
 *     <li><code>itemsPerPage</code> Sets how many items per page should be displayed. Default value is 10.
 *     </li>
 *     <li><code>maxSize</code> default value is 5.
 *     </li>
 *     <li><code>numPages</code> default value is 1.
 *     </li>
 *     <li><code>length</code> default value is 0.
 *     </li>
 *     <li><code>disableFiltering</code> If this value is set to true, the search bar will not be included.
 *     </li>
 *     <li><code>filterString</code>
 *     </li>
 *     <li><code>disableButtons</code> default value is `false`.
 *     </li>
 *     <li><code>enableEditButton</code> default value is `false`.
 *     </li>
 *     <li><code>enableIOButton</code> default value is `false`.
 *     </li>
 * </ul>
 *
 * <label>Outputs</label>
 * <ul>
 *     <li><code>cellSelected</code> emits the selected cell in the table. Contains the data of the whole selected row.
 *     </li>
 *     <li><code>removeBtnClicked</code> emits the selected cell in the table. Contains the data of the whole selected
 * row.
 *     </li>
 *     <li><code>addBtnClicked</code> emits the selected cell in the table. Contains the data of the whole selected
 * row.
 *     </li>
 *     <li><code>editBtnClicked</code> emits the selected cell in the table. Contains the data of the whole selected
 * row.
 *     </li>
 *     <li><code>ioBtnClicked</code> emits the selected cell in the table. Contains the data of the whole selected row.
 *     </li>
 *
 * @example <caption>Minimalistic example:</caption>
 * ```
 * ex.component.ts:
 *     dataArray: Array<ExampleData> = [
 *         {
 *             name: 'element name',
 *             condition: 'condition for this value',
 *             example: 'example for using this element'
 *         },
 *         { name: 'second element', condition: 'x < 42', example: 'y = 4' }
 *     ];
 *     columnsArray: Array<WineryTableColumn> = [
 *         { title: 'Name', name: 'name', sort: true },
 *         { title: 'Precondition', name: 'condition', sort: true },
 *         { title: 'Example usage', name: 'example', sort: true },
 *     ]
 *
 * ex.component.html:
 *     <winery-table
 *         [data]="dataArray"
 *         [columns]="columnsArray">
 *     </winery-table>
 * ```
 */
@Component({
    selector: 'winery-table',
    templateUrl: 'wineryTable.component.html',
    styleUrls: ['wineryTable.component.css']
})
export class WineryTableComponent implements OnInit, DoCheck {

    @ViewChild('tableContainer') tableContainer: any;
    @Input() title: string;
    @Input() itemsPerPage = 10;
    @Input() maxSize = 5;
    @Input() numPages = 1;
    @Input() length = 0;
    @Input() disableFiltering = false;
    @Input() data: Array<any> = [];
    @Input() columns: Array<WineryTableColumn>;
    @Input() filterString: string;
    @Input() config: any = {
        /**
         * switch on the paging plugin
         */
        paging: true,
        /**
         * switch on the sorting plugin
         */
        sorting: { columns: this.columns || true },
        /**
         * switch on the filtering plugin
         * {@link ColumnFilter}
         */
        filtering: { filterString: '' },
        /**
         * additional CSS classes that should be added to a table
         */
        className: ['table-striped', 'table-bordered']
    };
    @Input() disableButtons = false;
    @Input() enableEditButton = false;
    @Input() enableIOButton = false;

    @Output() cellSelected = new EventEmitter<any>();
    @Output() removeBtnClicked = new EventEmitter<any>();
    @Output() addBtnClicked = new EventEmitter<any>();
    @Output() editBtnClicked = new EventEmitter<any>();
    @Output() ioBtnClicked = new EventEmitter<any>();

    public rows: Array<any> = [];
    public page = 1;
    public currentSelected: any = null;

    private oldData: Array<any> = this.data;
    private oldLength = this.oldData.length;

    // region #######Table events and functions######

    public onChangeTable(config: any, page: any = { page: this.page, itemsPerPage: this.itemsPerPage }): any {
        this.currentSelected = null;
        this.refreshRowHighlighting();

        if (config.filtering) {
            Object.assign(this.config.filtering, config.filtering);
        }

        if (config.sorting) {
            Object.assign(this.config.sorting, config.sorting);
        }

        const filteredData = this.changeFilter(this.data, this.config);
        const sortedData = this.changeSort(filteredData, this.config);
        this.rows = page && config.paging ? this.changePage(page, sortedData) : sortedData;
        this.length = sortedData.length;
    }

    public changePage(page: any, data: Array<any> = this.data): Array<any> {
        const start = (page.page - 1) * page.itemsPerPage;
        const end = page.itemsPerPage > -1 ? (start + page.itemsPerPage) : data.length;
        return data.slice(start, end);
    }

    public changeSort(data: any, config: any): any {
        if (!config.sorting) {
            return data;
        }

        const columns = this.config.sorting.columns || [];
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

        const tempArray: Array<any> = [];
        filteredData.forEach((item: any) => {
            let flag = false;
            this.columns.forEach((column: any) => {
                if (!isNullOrUndefined(item[column.name]) && item[column.name].toString().match(this.config.filtering.filterString)) {
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

    onCellClick(data: WineryRowData) {
        this.cellSelected.emit(data);
        this.currentSelected = data.row;
        this.refreshRowHighlighting();
    }

    private refreshRowHighlighting(): void {
        const rowNumber: number = this.currentSelected ? this.rows.findIndex(row => row === this.currentSelected) : -1;
        const tableRows = this.tableContainer.nativeElement.children[0].children[0].children[1].children;

        for (let i = 0; i < tableRows.length; i++) {
            tableRows[i].className = (i === rowNumber) ? 'active-row' : '';
        }
    }

    onAddClick($event: Event) {
        $event.stopPropagation();
        this.addBtnClicked.emit();
    }

    onRemoveClick($event: Event) {
        $event.stopPropagation();
        this.removeBtnClicked.emit(this.currentSelected);
    }

    onEditClick($event: Event) {
        $event.stopPropagation();
        this.editBtnClicked.emit(this.currentSelected);
    }

    onIOClick($event: Event) {
        $event.stopPropagation();
        this.ioBtnClicked.emit(this.currentSelected);
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
            const newLength = this.data.length;
            const old = this.oldLength;
            if (old !== newLength) {
                // let direction = old < newLength ? 'grew' : 'shrunk';
                // this.logs.push(`heroes ${direction} from ${old} to ${newLength}`);
                this.oldLength = newLength;
                this.onChangeTable(this.config);
            }
        }
    }

}

/**
 * Interface to set the columns array.
 */
export interface WineryTableColumn {
    /**
     * @member title
     * Is required and used to name the column.
     */
    title: string;
    /**
     * @member name
     * Is required and used to identify the corresponding objects' property in the data array.
     */
    name: string;
    /**
     * @member sort
     * Is optional and defines whether the column should be sortable.
     */
    sort?: boolean;
    /**
     * @member className
     * Optional parameter to add classes to a column header
     *
     */
    className?: string | Array<string>;
    /**
     * switch on the filter plugin for this column
     * @member  filtering
     */
    filtering?: ColumnFilter;

}

/**
 * Interface for the filtering property of the WineryTableColumn
 */
export interface ColumnFilter {
    /**
     * the default value for filter
     */
    filterString: string;
    /**
     * the property name in raw data
     */
    columnName: string;
}

/**
 * Interface for data emitted by <code>cellSelected</code>
 */
export interface WineryRowData {
    row: any;
    column: string;
}
