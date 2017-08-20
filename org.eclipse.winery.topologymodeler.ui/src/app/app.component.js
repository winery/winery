"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
<<<<<<< HEAD
var core_1 = require("@angular/core");
var AppComponent = (function () {
    function AppComponent() {
        this.title = 'Tour of Heroes';
        this.hero = 'Windstorm';
    }
=======
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var ttopology_template_1 = require("./ttopology-template");
var AppComponent = (function () {
    function AppComponent(ngRedux, actions) {
        this.ngRedux = ngRedux;
        this.actions = actions;
        this.nodeTemplates = [];
        this.relationshipTemplates = [];
        this.testJson = {
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
        this.testVisuals = [
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
    }
    AppComponent.prototype.ngOnInit = function () {
        this.topologyTemplate = this.testJson;
        this.visuals = this.testVisuals;
        for (var _i = 0, _a = this.testJson.nodeTemplates; _i < _a.length; _i++) {
            var node = _a[_i];
            var color = void 0;
            var imageUrl = void 0;
            for (var _b = 0, _c = this.testVisuals; _b < _c.length; _b++) {
                var visual = _c[_b];
                if (visual.localName === node.name) {
                    color = visual.color;
                    imageUrl = visual.imageUrl;
                }
            }
            this.nodeTemplates.push(new ttopology_template_1.TNodeTemplate(undefined, node.id, node.type, node.name, node.minInstances, node.maxInstances, color, imageUrl, node.documentation, node.any, node.otherAttributes));
        }
        for (var i = 0; i < this.nodeTemplates.length; i++) {
            this.ngRedux.dispatch(this.actions.saveNodeTemplate(this.nodeTemplates[i]));
        }
        for (var _d = 0, _e = this.testJson.relationshipTemplates; _d < _e.length; _d++) {
            var relationship = _e[_d];
            this.relationshipTemplates.push(new ttopology_template_1.TRelationshipTemplate(relationship.sourceElement, relationship.targetElement, undefined, relationship.sourceElement.concat(relationship.targetElement)));
        }
        for (var i = 0; i < this.relationshipTemplates.length; i++) {
            this.ngRedux.dispatch(this.actions.saveRelationship(this.relationshipTemplates[i]));
        }
    };
>>>>>>> 7c21005... WIP
    return AppComponent;
}());
AppComponent = __decorate([
    core_1.Component({
<<<<<<< HEAD
        selector: 'topbar',
        template: "<div id=\"topbar\">\n\t\t            <button class=\"btn btn-success topbutton\" id=\"saveBtn\">Save</button>\n                <div class=\"btn-group\">\n                  <button class=\"btn btn-default\" onclick=\"doLayout();\">Layout</button>\n                  <button class=\"btn btn-default\" onclick=\"horizontalAlignment();\">Align-h (|)</button>\n                  <button class=\"btn btn-default\" onclick=\"verticalAlignment();\">Align-v (-)</button>\n                </div>\n\n                <div class=\"btn-group\" id=\"toggleButtons\">\n                  <button class=\"btn btn-default\" id=\"toggleIdVisibility\">Ids</button>\n                  <button class=\"btn active\" id=\"toggleTypeVisibility\">Types</button>\n                  <button class=\"btn btn-default\" id=\"togglePropertiesVisibility\">Properties</button>\n                  <button class=\"btn btn-default\" id=\"toggleDeploymentArtifactsVisibility\">Deployment Artifacts</button>\n                  <button class=\"btn btn-default\" id=\"toggleReqCapsVisibility\">Requirements &amp; Capabilities</button>\n                  <button class=\"btn btn-default\" id=\"PoliciesVisibility\">Policies</button>\n                  <button class=\"btn btn-default\" id=\"TargetLocationsVisibility\">Target Locations</button>\n                </div>\n                \n\t\t            <button data-toggle=\"button\" class=\"btn btn-default\">Print View</button>\n\n\t\t            <button class=\"btn btn-default\" id=\"splitBtn\">Split</button>\n\n\t\t            <button class=\"btn btn-default topbutton\" id=\"importBtn\">Import Topology</button>\n\n                <div class=\"btn-group\">\n                  <button type=\"button\" class=\"btn btn-default dropdown-toggle\">Other <span class=\"caret\"></span></button>\n            \n                  <ul class=\"dropdown-menu\" role=\"menu\">\n                    <li><a href=\"#\">Complete Topology</a></li>\n                    <li><a id=\"exportCSARbtn\" href=\"http://dev.winery.opentosca.org/winery/servicetemplates/http%253A%252F%252Fopentosca.org%252Fservicetemplates/FIWARE-Orion_Bare_Docker/topologytemplate/../?csar\" target=\"_blank\" data-original-title=\"\" title=\"\">Export CSAR</a></li>\n                    <li><a href=\"#\">about</a></li>\n                  </ul>\n                  \n                </div>\n            </div>\n            ",
    })
], AppComponent);
exports.AppComponent = AppComponent;
//# sourceMappingURL=app.component.js.map
=======
        selector: 'app-topologyrenderer',
        templateUrl: './app.component.html',
        styleUrls: ['./app.component.css']
    })
], AppComponent);
exports.AppComponent = AppComponent;
>>>>>>> 7c21005... WIP
