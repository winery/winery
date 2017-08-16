import { Injectable, OnInit } from '@angular/core';
import { Visuals } from '../ttopology-template';

@Injectable()
export class JsonService implements OnInit {

  testJson: any;
  visuals: Visuals[];
  mockNodesArray = [
    {
      documentation: [],
      any: [],
      otherAttributes: {},
      id: 'test',
      type: '{http://winery.opentosca.org/test/nodetypes/fruits}test',
      name: 'test',
      minInstances: 1,
      maxInstances: 1
    }
  ];
  mockRelationshipsArray = [
    {
      'sourceElement': 'baobab',
      'targetElement': 'tree'
    }
  ];
  mockVisuals = [{
    imageUrl: 'http://www.example.org/winery/test/nodetypes/' +
    'http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/appearance/50x50',
    color: '#89ee01',
    nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}baobab',
    localName: ''
  }];

  getRelationships(): any {
      return this.testJson.relationshipTemplates;

  }

  getNodes(): any {
      return this.testJson.nodeTemplates;

  }

  getVisuals(): any {
      return this.visuals;

  }

  setData(visuals: any, topologyTemplate: any) {
    // This causes "type not compatible error"
    this.visuals = visuals;
    this.testJson = topologyTemplate;
  }

  setVisuals(visuals: any) {
    this.visuals = visuals;
    // TODO Josip: replace with proper QName implementation: Parse localName from QName
    for (const visual of this.visuals) {
      visual.localName = visual.nodeTypeId.split('}')[1];
    }
  }

  setTopologyTemplate(topologyTemplate: any) {
    this.testJson = topologyTemplate;
  }

  constructor() {
  }

  ngOnInit() {
    // TODO visual local name

  }

}
