"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var animations_1 = require("@angular/animations");
var palette_service_1 = require("../palette.service");
var ttopology_template_1 = require("../ttopology-template");
var PaletteComponent = (function () {
    function PaletteComponent(paletteService, ngRedux, actions) {
        var _this = this;
        this.paletteService = paletteService;
        this.ngRedux = ngRedux;
        this.actions = actions;
        this.detailsAreHidden = true;
        this.paletteRootState = 'shrunk';
        this.paletteItems = [];
        this.allNodeTemplates = [];
        this.subscription = ngRedux.select('appState')
            .subscribe(function (newState) {
            _this.updateState(newState.currentPaletteOpenedState);
            _this.addNodes(newState.currentSavedJsonTopology.nodeTemplates);
        });
        this.paletteItems = paletteService.getPaletteData();
    }
    PaletteComponent.prototype.updateState = function (newPaletteOpenedState) {
        if (!newPaletteOpenedState) {
            this.paletteRootState = 'shrunk';
        }
    };
    PaletteComponent.prototype.addNodes = function (nodeTemplates) {
        if (nodeTemplates.length > 0) {
            if (this.allNodeTemplates.length === 0) {
                this.allNodeTemplates = nodeTemplates;
            }
            this.checkNodes(nodeTemplates);
        }
    };
    PaletteComponent.prototype.checkNodes = function (currentNodes) {
        if (currentNodes !== null) {
            var newNode = currentNodes[currentNodes.length - 1];
            if (this.allNodeTemplates.length !== 0) {
                var lastNodeId = this.allNodeTemplates[this.allNodeTemplates.length - 1].id;
                var newNodeId = newNode.id;
                if (lastNodeId !== newNodeId) {
                    this.allNodeTemplates.push(newNode);
                }
            }
            else {
                this.allNodeTemplates.push(newNode);
            }
        }
    };
    PaletteComponent.prototype.ngOnInit = function () {
    };
    PaletteComponent.prototype.openPalette = function () {
        this.detailsAreHidden = false;
        this.toggleRootState();
    };
    PaletteComponent.prototype.toggleRootState = function () {
        if (this.paletteRootState === 'shrunk') {
            this.paletteRootState = 'extended';
            this.ngRedux.dispatch(this.actions.sendPaletteOpened(true));
        }
        else {
            this.paletteRootState = 'shrunk';
            this.ngRedux.dispatch(this.actions.sendPaletteOpened(true));
        }
    };
    PaletteComponent.prototype.publishTitle = function ($event) {
        var left = ($event.pageX - 100).toString().concat('px');
        var top = ($event.pageY - 30).toString().concat('px');
        var name = $event.target.innerHTML;
        var otherAttributes = {
            otherAttributes: {
                location: 'undefined',
                x: left,
                y: top
            },
        };
        var newId = this.generateId(name);
        var paletteItem = new ttopology_template_1.TNodeTemplate(undefined, newId, undefined, name, 1, 1, undefined, undefined, undefined, undefined, otherAttributes);
        this.ngRedux.dispatch(this.actions.saveNodeTemplate(paletteItem));
    };
    PaletteComponent.prototype.generateId = function (name) {
        var nodeArrayLength = this.allNodeTemplates.length;
        if (this.allNodeTemplates.length > 0) {
            for (var i = nodeArrayLength - 1; i >= 0; i--) {
                if (name === this.allNodeTemplates[i].name) {
                    var idOfCurrentNode = this.allNodeTemplates[i].id;
                    var numberOfNewInstance = parseInt(idOfCurrentNode.substring(idOfCurrentNode.indexOf('_') + 1), 10) + 1;
                    var newId = name.concat('_', numberOfNewInstance.toString());
                    return newId;
                }
            }
        }
    };
    PaletteComponent.prototype.ngOnDestroy = function () {
        this.subscription.unsubscribe();
    };
    return PaletteComponent;
}());
PaletteComponent = __decorate([
    core_1.Component({
        selector: 'app-palette-component',
        templateUrl: './palette.component.html',
        styleUrls: ['./palette.component.css'],
        providers: [palette_service_1.PaletteService],
        animations: [
            animations_1.trigger('paletteRootState', [
                animations_1.state('shrunk', animations_1.style({
                    height: '100px',
                    width: '40px',
                })),
                animations_1.state('extended', animations_1.style({
                    height: '40px',
                    width: '100%',
                })),
                animations_1.transition('shrunk => extended', animations_1.animate('200ms ease-out')),
                animations_1.transition('extended => shrunk', animations_1.animate('200ms ease-out'))
            ]),
            animations_1.trigger('paletteRootTextState', [
                animations_1.state('shrunk', animations_1.style({
                    transform: 'rotate(270deg)',
                    textAlign: 'center',
                    marginTop: '40px',
                })),
                animations_1.state('extended', animations_1.style({
                    textAlign: 'center',
                    marginTop: '0px',
                    transform: 'rotate(360deg)',
                })),
                animations_1.transition('shrunk => extended', animations_1.animate('200ms ease-out')),
                animations_1.transition('extended => shrunk', animations_1.animate('200ms ease-out'))
            ]),
            animations_1.trigger('paletteItemState', [
                animations_1.state('shrunk', animations_1.style({
                    display: 'none',
                    opacity: '0',
                    width: '40px',
                })),
                animations_1.state('extended', animations_1.style({
                    display: 'block',
                    opacity: '1',
                    width: '100%',
                })),
                animations_1.transition('shrunk => extended', animations_1.animate('200ms ease-out')),
                animations_1.transition('extended => shrunk', animations_1.animate('200ms ease-out'))
            ])
        ]
    })
], PaletteComponent);
exports.PaletteComponent = PaletteComponent;
