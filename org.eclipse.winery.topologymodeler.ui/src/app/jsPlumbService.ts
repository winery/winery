/**
 * Created by ThommyZ on 31.05.2017.
 */
import { Injectable } from '@angular/core';

declare const jsPlumb: any;

@Injectable()
export class JsPlumbService {

  getJsPlumbInstance(): any {
    jsPlumb.ready(() => {
    });
    return jsPlumb.getInstance({
      PaintStyle: {
        strokeWidth: 2,
        stroke: 'rgba(55,55,55,0.9)',
      },
      Connector: ['StateMachine', {proximityLimit: 600, curviness: 30}],
      Endpoints: [
        ['Blank', {radius: 0}], ['Blank', {radius: 0}]],
      ConnectionsDetachable: false,
      Anchor: 'Continuous',
      Anchors: [
        ['Perimeter', {shape: 'Rectangle'}],
        [ 'Perimeter', { shape: 'Rectangle'} ]
      ],
    });
  }
}
