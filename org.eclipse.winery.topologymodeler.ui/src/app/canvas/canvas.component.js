"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var layout_directive_1 = require("../layout.directive");
var CanvasComponent = (function () {
    /*
    @select(appState => appState.currentSavedJsonTopology) readonly currentSavedJsonTopology: Observable<any>;
    @select(appState => appState.currentEnhanceGridState) readonly currentPaletteOpenedState: Observable<any>;
    */
    function CanvasComponent(jsPlumbService, jsonService, _eref, _layoutDirective, differsPressedNavBarButton, ngRedux, actions) {
        var _this = this;
        this.jsPlumbService = jsPlumbService;
        this.jsonService = jsonService;
        this._eref = _eref;
        this._layoutDirective = _layoutDirective;
        this.ngRedux = ngRedux;
        this.actions = actions;
        this.paletteClicked = false;
        this.nodeTemplates = [];
        this.allNodeTemplates = [];
        this.allRelationshipTemplates = [];
        this.relationshipTemplates = [];
        this.nodeTypes = [];
        this.selectedNodes = [];
        this.nodeSelected = false;
        this.nodeArrayEmpty = false;
        this.offsetY = 90;
        this.offsetX = 100;
        this.crosshair = false;
        this.subscription = ngRedux.select('appState')
            .subscribe(function (newState) {
            _this.updateGridState(newState.currentPaletteOpenedState);
            _this.addTopology(newState.currentSavedJsonTopology);
        });
        this.differPressedNavBarButton = differsPressedNavBarButton.find([]).create(null);
    }
    /*
    createNewNode(newPaletteItem: any) {
      if (newPaletteItem) {
        console.log(newPaletteItem);
        if (this.paletteClicked === false) {
          this.paletteClicked = true;
        }
        this.nodeFactory(newPaletteItem);
      }
    }
  */
    CanvasComponent.prototype.updateGridState = function (currentPaletteOpenedState) {
        if (currentPaletteOpenedState !== true) {
            this.enhanceGrid = 0;
            this.offsetX = 0;
        }
        else {
            this.offsetX = -200;
            this.enhanceGrid = 200;
        }
    };
    CanvasComponent.prototype.addTopology = function (currentSavedJsonTopology) {
        if (currentSavedJsonTopology.nodeTemplates.length > 0) {
            if (this.allNodeTemplates.length === 0) {
                this.allNodeTemplates = currentSavedJsonTopology.nodeTemplates;
            }
            this.addNodes(currentSavedJsonTopology.nodeTemplates);
        }
        if (currentSavedJsonTopology.relationshipTemplates.length > 0) {
            if (this.allRelationshipTemplates.length === 0) {
                this.allRelationshipTemplates = currentSavedJsonTopology.relationshipTemplates;
            }
            this.addRelationships(currentSavedJsonTopology.relationshipTemplates);
        }
    };
    CanvasComponent.prototype.addNodes = function (currentNodes) {
        var newNode = currentNodes[currentNodes.length - 1];
        if (this.allNodeTemplates.length > 0) {
            var lastNodeId = this.allNodeTemplates[this.allNodeTemplates.length - 1].id;
            var newNodeId = newNode.id;
            if (lastNodeId !== newNodeId) {
                this.allNodeTemplates.push(newNode);
            }
        }
        else {
            this.allNodeTemplates.push(newNode);
        }
    };
    CanvasComponent.prototype.addRelationships = function (currentRelationships) {
        var _this = this;
        var newRelationship = currentRelationships[currentRelationships.length - 1];
        if (this.allRelationshipTemplates.length > 0) {
            var lastRelationshipId = this.allRelationshipTemplates[this.allRelationshipTemplates.length - 1].id;
            var newRelationshipId = newRelationship.id;
            if (lastRelationshipId !== newRelationshipId) {
                this.allRelationshipTemplates.push(newRelationship);
                setTimeout(function () { return _this.displayRelationships(newRelationship); }, 1);
            }
        }
        else {
            this.allRelationshipTemplates.push(newRelationship);
            setTimeout(function () { return _this.displayRelationships(newRelationship); }, 1);
        }
    };
    CanvasComponent.prototype.displayRelationships = function (newRelationship) {
        var sourceElement = newRelationship.sourceElement;
        var targetElement = newRelationship.targetElement;
        /*
        this.newJsPlumbInstance.draggable(sourceElement);
        this.newJsPlumbInstance.draggable(targetElement);
        */
        this.newJsPlumbInstance.connect({
            source: sourceElement,
            target: targetElement,
            overlays: [['Arrow', { width: 15, length: 15, location: 1, id: 'arrow', direction: 1 }],
                ['Label', {
                        label: '(Hosted On)',
                        id: 'label',
                        labelStyle: { font: 'bold 18px/30px Courier New, monospace' }
                    }]
            ],
        });
    };
    CanvasComponent.prototype.onClick = function ($event) {
        if (this._eref.nativeElement.contains($event.target) && this.longPress === false) {
            this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes);
            this.clearArray(this.selectedNodes);
            if ($event.clientX > 200) {
                this.ngRedux.dispatch(this.actions.sendPaletteOpened(false));
            }
        }
    };
    CanvasComponent.prototype.clearArray = function (array) {
        array.length = 0;
    };
    CanvasComponent.prototype.showSelectionRange = function ($event) {
        if (($event.pageY - this.offsetY) > 0) {
            this.selectionActive = true;
            this.pageX = $event.pageX + this.offsetX;
            this.pageY = $event.pageY - this.offsetY;
            this.initialW = $event.pageX;
            this.initialH = $event.pageY;
            this.callOpenSelector = true;
            this.callSelectItems = true;
        }
        this.crosshair = true;
    };
    CanvasComponent.prototype.openSelector = function ($event) {
        if (this.callOpenSelector) {
            this.selectionWidth = Math.abs(this.initialW - $event.pageX);
            this.selectionHeight = Math.abs(this.initialH - $event.pageY);
            if ($event.pageX <= this.initialW && $event.pageY >= this.initialH) {
                this.pageX = $event.pageX + this.offsetX;
            }
            else if ($event.pageY <= this.initialH && $event.pageX >= this.initialW) {
                this.pageY = $event.pageY - this.offsetY;
            }
            else if ($event.pageY < this.initialH && $event.pageX < this.initialW) {
                this.pageX = $event.pageX + this.offsetX;
                this.pageY = $event.pageY - this.offsetY;
            }
        }
    };
    CanvasComponent.prototype.selectElements = function ($event) {
        if (this.callSelectItems) {
            this.callOpenSelector = false;
            this.callSelectItems = false;
            for (var _i = 0, _a = this.allNodeTemplates; _i < _a.length; _i++) {
                var node = _a[_i];
                var aElem = document.getElementById('selection');
                var bElem = document.getElementById(node.id);
                var result = this.doObjectsCollide(aElem, bElem);
                if (result === true) {
                    this.enhanceDragSelection(node.id);
                }
            }
            this.crosshair = false;
            this.selectionActive = false;
            this.selectionWidth = 0;
            this.selectionHeight = 0;
        }
    };
    CanvasComponent.prototype.getOffset = function (el) {
        var _x = 0;
        var _y = 0;
        while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
            _x += el.offsetLeft - el.scrollLeft;
            _y += el.offsetTop - el.scrollTop;
            el = el.offsetParent;
        }
        return { top: _y, left: _x };
    };
    CanvasComponent.prototype.doObjectsCollide = function (a, b) {
        var aTop = this.getOffset(a).top;
        var aLeft = this.getOffset(a).left;
        var bTop = this.getOffset(b).top;
        var bLeft = this.getOffset(b).left;
        return !(((aTop + a.getBoundingClientRect().height) < (bTop)) ||
            (aTop > (bTop + b.getBoundingClientRect().height)) ||
            ((aLeft + a.getBoundingClientRect().width) < bLeft) ||
            (aLeft > (bLeft + b.getBoundingClientRect().width)));
    };
    CanvasComponent.prototype.repaintJsPlumb = function () {
        this.newJsPlumbInstance.repaintEverything();
    };
    CanvasComponent.prototype.ngDoCheck = function () {
        var pressedNavBarButton = this.differPressedNavBarButton.diff(this.pressedNavBarButton);
        if (pressedNavBarButton) {
            if (pressedNavBarButton._mapHead.currentValue === 'layout') {
                this._layoutDirective.layoutNodes(this.allNodeTemplates, this.relationshipTemplates, this.newJsPlumbInstance);
            }
            if (pressedNavBarButton._mapHead.currentValue === 'alignv') {
                this._layoutDirective.alignVertical(this.allNodeTemplates, this.newJsPlumbInstance);
            }
            if (pressedNavBarButton._mapHead.currentValue === 'alignh') {
                this._layoutDirective.alignHorizontal(this.allNodeTemplates, this.newJsPlumbInstance);
            }
        }
    };
    CanvasComponent.prototype.ngOnInit = function () {
        this.newJsPlumbInstance = this.jsPlumbService.getJsPlumbInstance();
        this.newJsPlumbInstance.setContainer('container');
        /*
        this.nodeTemplates = this.jsonService.getNodes();
        console.log(this.nodeTemplates);
        */
        this.relationshipTemplates = this.jsonService.getRelationships();
        this.visuals = this.jsonService.getVisuals();
        this.assignVisuals();
    };
    CanvasComponent.prototype.assignVisuals = function () {
        this.visuals = this.jsonService.getVisuals();
        for (var _i = 0, _a = this.allNodeTemplates; _i < _a.length; _i++) {
            var node = _a[_i];
            for (var _b = 0, _c = this.visuals; _b < _c.length; _b++) {
                var visual = _c[_b];
                // console.log('node.id = ' + node.id);
                // console.log('visual = ' + JSON.stringify(visual));
                if (node.id === visual.localName || node.id.startsWith(visual.localName + '_')) {
                    node.color = visual.color;
                    if (visual.hasOwnProperty('imageUrl')) {
                        node.imageUrl = visual.imageUrl;
                    }
                }
            }
        }
    };
    CanvasComponent.prototype.makeDraggable = function ($event) {
        this.newJsPlumbInstance.draggable($event);
    };
    CanvasComponent.prototype.ngAfterViewInit = function () {
        /*
        for (let i = 0; i < this.allRelationshipTemplates.length; i++) {
          const sourceElement = this.relationshipTemplates[i].sourceElement;
          const targetElement = this.relationshipTemplates[i].targetElement;
          this.newJsPlumbInstance.draggable(sourceElement);
          this.newJsPlumbInstance.draggable(targetElement);
          const connection = new TRelationshipTemplate(sourceElement, targetElement);
          this.newJsPlumbInstance.connect({
            source: connection.sourceElement,
            target: connection.targetElement,
            overlays: [['Arrow', {width: 15, length: 15, location: 1, id: 'arrow', direction: 1}],
              ['Label', {
                label: '(Hosted On)',
                id: 'label',
                labelStyle: {font: 'bold 18px/30px Courier New, monospace'}
              }]
            ],
          });
        }
        */
    };
    CanvasComponent.prototype.checkingNodeSelectionForDuplicateIDs = function (id) {
        this.nodeSelected = false;
        for (var _i = 0, _a = this.selectedNodes; _i < _a.length; _i++) {
            var node = _a[_i];
            if (node === id) {
                this.nodeSelected = true;
            }
        }
        if (this.nodeSelected === false) {
            this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes);
            this.clearArray(this.selectedNodes);
        }
    };
    CanvasComponent.prototype.checkIfNodeInSelection = function ($event) {
        this.checkingNodeSelectionForDuplicateIDs($event);
    };
    CanvasComponent.prototype.arrayContainsNode = function (arrayOfNodes, id) {
        if (arrayOfNodes !== null && arrayOfNodes.length > 0) {
            for (var i = 0; i < arrayOfNodes.length; i++) {
                if (arrayOfNodes[i] === id) {
                    return true;
                }
            }
        }
        return false;
    };
    CanvasComponent.prototype.enhanceDragSelection = function (id) {
        this.nodeArrayEmpty = false;
        this.newJsPlumbInstance.addToPosse(id, 'dragSelection');
        this.nodeArrayEmpty = this.arrayContainsNode(this.selectedNodes, id);
        if (!this.nodeArrayEmpty) {
            this.selectedNodes.push(id);
        }
    };
    CanvasComponent.prototype.addNodeToDragSelection = function ($event) {
        this.enhanceDragSelection($event);
    };
    CanvasComponent.prototype.trackTimeOfMouseDown = function (e) {
        this.startTime = new Date().getTime();
    };
    CanvasComponent.prototype.trackTimeOfMouseUp = function (e) {
        this.endTime = new Date().getTime();
        this.testTimeDifference();
    };
    CanvasComponent.prototype.testTimeDifference = function () {
        if ((this.endTime - this.startTime) < 250) {
            this.longPress = false;
        }
        else if (this.endTime - this.startTime >= 300) {
            this.longPress = true;
        }
    };
    CanvasComponent.prototype.ngOnDestroy = function () {
        this.subscription.unsubscribe();
    };
    return CanvasComponent;
}());
__decorate([
    core_1.Input()
], CanvasComponent.prototype, "pressedNavBarButton", void 0);
__decorate([
    core_1.HostListener('click', ['$event'])
], CanvasComponent.prototype, "onClick", null);
__decorate([
    core_1.HostListener('mousedown', ['$event'])
], CanvasComponent.prototype, "showSelectionRange", null);
__decorate([
    core_1.HostListener('mousemove', ['$event'])
], CanvasComponent.prototype, "openSelector", null);
__decorate([
    core_1.HostListener('mouseup', ['$event'])
], CanvasComponent.prototype, "selectElements", null);
CanvasComponent = __decorate([
    core_1.Component({
        selector: 'app-canvas',
        providers: [layout_directive_1.LayoutDirective],
        templateUrl: './canvas.component.html',
        styleUrls: ['./canvas.component.css']
    })
], CanvasComponent);
exports.CanvasComponent = CanvasComponent;
