import {Component, OnInit} from '@angular/core';
import {TNodeTemplate, TRelationshipTemplate} from './ttopology-template';
import {IAppState} from './redux/store/app.store';
import {AppActions} from './redux/actions/app.actions';
import {NgRedux} from '@angular-redux/store';

@Component({
  selector: 'app-topologyrenderer',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  topologyTemplate: any;
  visuals: any;
  nodeTemplates: Array<TNodeTemplate> = [];
  relationshipTemplates: Array<TRelationshipTemplate> = [];

  testJson = {
    documentation: [],
    any: [],
    otherAttributes: {},
    nodeTemplates: [
      {
        documentation: [],
        any: [],
        otherAttributes: {
          location: 'undefined',
          x: 600,
          y: 49
        },
        id: 'plantage',
        type: '{http://winery.opentosca.org/test/nodetypes/fruits}plantage',
        name: 'plantage',
        minInstances: 1,
        maxInstances: 1
      },
      {
        documentation: [],
        any: [],
        otherAttributes: {
          location: 'undefined',
          x: 600,
          y: 267
        },
        id: 'tree',
        type: '{http://winery.opentosca.org/test/nodetypes/fruits}tree',
        name: 'tree',
        minInstances: 1,
        maxInstances: 1
      },
      {
        documentation: [],
        any: [],
        otherAttributes: {
          location: 'undefined',
          x: 600,
          y: 785
        },
        id: 'baobab',
        type: '{http://winery.opentosca.org/test/nodetypes/fruits}baobab',
        name: 'baobab',
        minInstances: 1,
        maxInstances: 1
      },
      {
        documentation: [],
        any: [],
        otherAttributes: {
          location: 'undefined',
          x: 958,
          y: 794
        },
        id: 'banana',
        type: '{http://winery.opentosca.org/test/nodetypes/fruits}banana',
        name: 'banana',
        minInstances: 1,
        maxInstances: 1
      },
      {
        documentation: [],
        any: [],
        otherAttributes: {
          location: 'undefined',
          x: 214,
          y: 764
        },
        id: 'mango',
        type: '{http://winery.opentosca.org/test/nodetypes/fruits}mango',
        name: 'mango',
        minInstances: 1,
        maxInstances: 1
      }
    ],
    relationshipTemplates: [
      {
        'sourceElement': 'baobab',
        'targetElement': 'tree'
      },
      {
        'sourceElement': 'banana',
        'targetElement': 'tree'
      },
      {
        'sourceElement': 'mango',
        'targetElement': 'tree'
      },
      {
        'sourceElement': 'banana',
        'targetElement': 'mango'
      },
      {
        'sourceElement': 'baobab',
        'targetElement': 'plantage'
      }
    ]
  };

  testVisuals = [
    {
      imageUrl: 'http://www.example.org/winery/test/nodetypes/' +
      'http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/appearance/50x50',
      color: '#89ee01',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}baobab',
      localName: 'baobab'
    },
    {
      imageUrl: '',
      color: '#89ee01',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}grape',
      localName: 'grape'
    },
    {
      imageUrl: '',
      color: '#89ee01',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}lemon',
      localName: 'lemon'
    },
    {
      imageUrl: '',
      color: '#89ee01',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}mango',
      localName: 'mango'
    },
    {
      imageUrl: '',
      color: '#01ace2',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}oat',
      localName: 'oat'
    },
    {
      imageUrl: '',
      color: '#FF7F50',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}orange',
      localName: 'orange'
    },
    {
      imageUrl: '',
      color: '#cb1016',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}pasture',
      localName: 'pasture'
    },
    {
      imageUrl: '',
      color: '#6f02b4',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}plantage',
      localName: 'plantage'
    },
    {
      imageUrl: '',
      color: '#bb1c9a',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}shetland_pony',
      localName: 'shetland_pony'
    },
    {
      imageUrl: '',
      color: '#8ac3a0',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}stall',
      localName: 'stall'
    },
    {
      imageUrl: '',
      color: '#8b0227',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}straw',
      localName: 'straw'
    },
    {
      imageUrl: '',
      color: '#36739e',
      nodeTypeId: '{http://winery.opentosca.org/test/nodetypes/fruits}tree',
      localName: 'tree'
    },
    {
      imageUrl: '',
      color: '#458ac5',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}trough',
      localName: 'trough'
    },
    {
      imageUrl: '',
      color: '#e47c98',
      nodeTypeId: '{http://winery.opentosca.org/test/ponyuniverse}banana',
      localName: 'banana'
    }
  ];

  constructor(private ngRedux: NgRedux<IAppState>, private actions: AppActions) {
  }

  
  ngOnInit() {
    this.topologyTemplate = this.testJson;
    this.visuals = this.testVisuals;
    for (const node of this.testJson.nodeTemplates) {
      let color;
      let imageUrl;
      for (const visual of this.testVisuals) {
        if (visual.localName === node.name) {
          color = visual.color;
          imageUrl = visual.imageUrl;
        }
      }
      this.nodeTemplates.push(
        new TNodeTemplate(
          undefined,
          node.id,
          node.type,
          node.name,
          node.minInstances,
          node.maxInstances,
          color,
          imageUrl,
          node.documentation,
          node.any,
          node.otherAttributes
        )
      );
    }
    for (let i = 0; i < this.nodeTemplates.length; i++) {
      this.ngRedux.dispatch(this.actions.saveNodeTemplate(this.nodeTemplates[i]));
    }
    for (const relationship of this.testJson.relationshipTemplates) {
      this.relationshipTemplates.push(
        new TRelationshipTemplate(
          relationship.sourceElement,
          relationship.targetElement,
          undefined,
          relationship.sourceElement.concat(relationship.targetElement),
        )
      );
    }
    for (let i = 0; i < this.relationshipTemplates.length; i++) {
      this.ngRedux.dispatch(this.actions.saveRelationship(this.relationshipTemplates[i]));
    }
  }
}
