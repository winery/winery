/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Josip Ledic - initial API and implementation, Refactoring to use Redux instead
 */
import {
  AfterViewInit,
  Component,
  DoCheck,
  EventEmitter,
  Input,
  IterableDiffers,
  KeyValueDiffers,
  OnInit,
  Output
} from '@angular/core';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { NgRedux } from '@angular-redux/store';
import {IAppState} from '../redux/store/app.store';
import {ButtonsStateModel} from '../models/buttonsState.model';

@Component({
  selector: 'app-node',
  templateUrl: './node.component.html',
  styleUrls: ['./node.component.css'],
})
export class NodeComponent implements OnInit, AfterViewInit, DoCheck {
  public items: string[] = ['Item 1', 'Item 2', 'Item 3'];
  public accordionGroupPanel = 'accordionGroupPanel';
  public customClass = 'customClass';
  connectorEndpointVisible = false;
  startTime;
  endTime;
  longpress = false;
  makeSelectionVisible = false;
  @Input() title: string;
  @Input() left: number;
  @Input() top: number;
  @Output() sendId: EventEmitter<string>;
  @Input() nodeColor: string;
  @Input() nodeImageUrl: string;
  @Output() askForRepaint: EventEmitter<string>;
  @Input() navBarButtonClicked: any;
  @Output() addNodeToDragSelection: EventEmitter<any>;
  @Output() checkIfNodeInSelection: EventEmitter<string>;
  @Input() selectedNodes: any[] = [];
  @Input() navbarButtonsState: ButtonsStateModel;
  differSelectedNodes: any;
  differNavBar: any;
  differUnselectedNodes: any;

  public addItem(): void {
    this.items.push(`Items ${this.items.length + 1}`);
  }

  constructor(differsSelectedNodes: IterableDiffers,
              differsNavBar: KeyValueDiffers,
              differsUnselectedNodes: IterableDiffers,
              private ngRedux: NgRedux<IAppState>,
              private actions: TopologyRendererActions) {
    this.sendId = new EventEmitter();
    this.askForRepaint = new EventEmitter();
    this.addNodeToDragSelection = new EventEmitter();
    this.checkIfNodeInSelection = new EventEmitter();
    this.differSelectedNodes = differsSelectedNodes.find([]).create(null);
    this.differNavBar = differsNavBar.find([]).create(null);
    this.differUnselectedNodes = differsUnselectedNodes.find([]).create(null);
  }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    this.sendId.emit(this.title);
  }

  ngDoCheck(): void {
    const selectedNodes = this.differSelectedNodes.diff(this.selectedNodes);

    if (selectedNodes) {
      selectedNodes.forEachAddedItem(r => {
          if (this.title === r.item) {
            this.makeSelectionVisible = true;
          }
        }
      );
      selectedNodes.forEachRemovedItem(r => {
          if (this.title === r.item) {
            this.makeSelectionVisible = false;
          }
        }
      );
    }
  }

  trackTimeOfMouseDown(): void {
    this.startTime = new Date().getTime();
  }

  trackTimeOfMouseUp(): void {
    this.endTime = new Date().getTime();
    this.testTimeDifference();
  }

  private testTimeDifference(): void {
    if ((this.endTime - this.startTime) < 250) {
      this.longpress = false;
    } else if (this.endTime - this.startTime >= 300) {
      this.longpress = true;
    }
  }

  showConnectorEndpoint($event): void {
    $event.stopPropagation();
    if ($event.ctrlKey) {
      this.addNodeToDragSelection.emit(this.title);
      this.makeSelectionVisible = !this.makeSelectionVisible;
    } else {
      (this.longpress) ? $event.preventDefault() : this.connectorEndpointVisible = !this.connectorEndpointVisible;
      this.checkIfNodeInSelection.emit(this.title);
    }
  }
}
