/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Josip Ledic - initial API and implementation
 */
import {
  Component, Input, OnInit,
  ViewContainerRef
} from '@angular/core';
import { WineryAlertService } from '../winery-alert/winery-alert.service';
import { JsonService } from '../jsonService/json.service';
import { Visuals } from '../ttopology-template';

@Component({
  selector: 'app-topology-renderer',
  templateUrl: './topology-renderer.component.html',
  styleUrls: ['./topology-renderer.component.css']
})
export class TopologyRendererComponent implements OnInit {

  @Input() topologyTemplate: any;
  @Input() visuals: Visuals[] = [new Visuals('red', 'apple', 'apple', 'abc')];
  pressedNavBarButton: any;

  constructor(vcr: ViewContainerRef, private notify: WineryAlertService,
              private jsonService: JsonService) {
    this.notify.init(vcr);
  }

  ngOnInit() {
    this.jsonService.setVisuals(this.visuals);
    this.jsonService.setTopologyTemplate(this.topologyTemplate);
  }

  sendPressedNavBarButtonToCanvas($event): void {
    this.pressedNavBarButton = $event;
  }
}
