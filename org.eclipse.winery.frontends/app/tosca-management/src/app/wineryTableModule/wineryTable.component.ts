/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { Component, DoCheck, EventEmitter, Input, IterableDiffer, IterableDiffers, OnInit, Output, ViewChild } from '@angular/core';

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
    @Input() columns: Array<WineryTableColumn> = [];
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
    @Input() showAddButton = true;
    @Input() showRemoveButton = true;
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
    private selectedRow = -1;

    /**
     * checks if input data changed
     */
    private iterableDiffer: IterableDiffer<any>;

    constructor(private iterableDiffers: IterableDiffers) {
        this.iterableDiffer = iterableDiffers.find([]).create(null);
    }

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
        this.rows = (page && config.paging ? this.changePage(page, sortedData) : sortedData)
            .map((r: any) => { return this.applyDisplay(r); });
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
                if (item[column.name] && item[column.name].toString().match(this.config.filtering.filterString)) {
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
        // account for pagination to get the actual data
        const rawIndex = this.rows.indexOf(data.row);
        const index = this.page ? (this.page - 1) * this.itemsPerPage + rawIndex : rawIndex;

        this.selectedRow = rawIndex;
        // monkey-patch the data row
        data.row = this.data[index];
        this.cellSelected.emit(data);
        this.currentSelected = data.row;
        this.refreshRowHighlighting();
    }

    private refreshRowHighlighting(): void {
        const tableRows = this.tableContainer.nativeElement.children[0].children[0].children[1].children;

        for (let i = 0; i < tableRows.length; i++) {
            tableRows[i].className = (i === this.selectedRow) ? 'active-row' : '';
        }
    }

    private applyDisplay(row: any): any {
        if (row === null || row === undefined) {
            return row;
        }
        if (!this.columns.some(coldef => coldef.display !== undefined)) {
            return row;
        }
        const result = {};
        Object.assign(result, row);
        for (const displayColumn of this.columns) {
            if (displayColumn.display !== undefined) {
                result[displayColumn.name] = displayColumn.display(row[displayColumn.name]);
            }
        }
        return result;
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

    ngOnInit() {
        this.config.sorting.columns = this.columns;
        this.length = this.data.length;
        this.onChangeTable(this.config);
    }

    /**
     * checks for changes in this.data and refreshes table if they occur
     * as the OnChanges Lifecycle Hook will only trigger when the input properties instance changes
     */
    ngDoCheck() {
        const changes = this.iterableDiffer.diff(this.data);
        if (changes) {
            this.onChangeTable(this.config);
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
    /**
     * Defines a function that maps the raw data elements of this column to a string for display purposes.
     */
    display?: (value: any) => string;
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
