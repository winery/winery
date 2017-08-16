import {
  Component,
  DoCheck,
  ElementRef,
  HostListener,
  Input,
  KeyValueDiffers, OnDestroy,
  OnInit,
} from '@angular/core';
import { JsPlumbService } from '../jsPlumbService';
import { JsonService } from '../jsonService/json.service';
import {TNodeTemplate, TRelationshipTemplate, TTopologyTemplate} from '../ttopology-template';
import { LayoutDirective } from '../layout.directive';
import {AppActions} from '../redux/actions/app.actions';
import {NgRedux, select} from '@angular-redux/store';
import {IAppState} from '../redux/store/app.store';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'app-canvas',
  providers: [LayoutDirective],
  templateUrl: './canvas.component.html',
  styleUrls: ['./canvas.component.css']
})
export class CanvasComponent implements OnInit, DoCheck, OnDestroy {
  paletteClicked = false;
  nodeTemplates: any[] = [];
  allNodeTemplates: Array<TNodeTemplate> = [];
  allRelationshipTemplates: Array<TRelationshipTemplate> = [];
  relationshipTemplates: Array<TRelationshipTemplate> = [];
  nodeTypes: any[] = [];
  selectedNodes: string[] = [];
  newJsPlumbInstance: any;
  visuals: any[];
  @Input() pressedNavBarButton: any;
  nodeSelected = false;
  nodeArrayEmpty = false;
  pageX: Number;
  pageY: Number;
  selectionActive: boolean;
  initialW: number;
  initialH: number;
  selectionWidth: number;
  selectionHeight: number;
  callOpenSelector: boolean;
  callSelectItems: boolean;
  offsetY = 32;
  offsetX = 102;
  startTime: number;
  endTime: number;
  longPress: boolean;
  crosshair = false;
  differPressedNavBarButton: any;
  enhanceGrid: number;
  subscription;
  /*
  @select(appState => appState.currentSavedJsonTopology) readonly currentSavedJsonTopology: Observable<any>;
  @select(appState => appState.currentEnhanceGridState) readonly currentPaletteOpenedState: Observable<any>;
  */

  constructor(private jsPlumbService: JsPlumbService, private jsonService: JsonService, private _eref: ElementRef,
              private _layoutDirective: LayoutDirective,
              differsPressedNavBarButton: KeyValueDiffers,
              private ngRedux: NgRedux<IAppState>,
              private actions: AppActions) {
    this.subscription = ngRedux.select<any>('appState')
      .subscribe(newState => {
        this.updateGridState(newState.currentPaletteOpenedState);
        this.addTopology(newState.currentSavedJsonTopology);
      });
    this.differPressedNavBarButton = differsPressedNavBarButton.find([]).create(null);
  }

  updateGridState(currentPaletteOpenedState: boolean) {
    if (currentPaletteOpenedState !== true) {
      this.enhanceGrid = 0;
      this.offsetX = 0;
    } else {
      this.offsetX = -200;
      this.enhanceGrid = 200;
    }
  }

  addTopology(currentSavedJsonTopology: TTopologyTemplate): void {
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
  }

  addNodes(currentNodes: Array<TNodeTemplate>) {
    const newNode = currentNodes[currentNodes.length - 1];
    if (this.allNodeTemplates.length > 0) {
      const lastNodeId = this.allNodeTemplates[this.allNodeTemplates.length - 1].id;
      const newNodeId = newNode.id;
      if (lastNodeId !== newNodeId) {
        this.allNodeTemplates.push(newNode);
        console.log(newNode.otherAttributes.x);
      }
    } else {
      this.allNodeTemplates.push(newNode);
    }
  }

  addRelationships(currentRelationships: Array<TRelationshipTemplate>) {
    const newRelationship = currentRelationships[currentRelationships.length - 1];
    if (this.allRelationshipTemplates.length > 0) {
      const lastRelationshipId = this.allRelationshipTemplates[this.allRelationshipTemplates.length - 1].id;
      const newRelationshipId = newRelationship.id;
      if (lastRelationshipId !== newRelationshipId) {
        this.allRelationshipTemplates.push(newRelationship);
        setTimeout(() => this.displayRelationships(newRelationship), 1);
      }
    } else {
      this.allRelationshipTemplates.push(newRelationship);
      setTimeout(() => this.displayRelationships(newRelationship), 1);
    }
  }

  displayRelationships(newRelationship: TRelationshipTemplate): void {
    const sourceElement = newRelationship.sourceElement;
    const targetElement = newRelationship.targetElement;
    this.newJsPlumbInstance.connect({
      source: sourceElement,
      target: targetElement,
      overlays: [['Arrow', {width: 15, length: 15, location: 1, id: 'arrow', direction: 1}],
        ['Label', {
          label: '(Hosted On)',
          id: 'label',
          labelStyle: {font: 'bold 18px/30px Courier New, monospace'}
        }]
      ],
    });
  }

  @HostListener('click', ['$event'])
  onClick($event) {
    if (this._eref.nativeElement.contains($event.target) && this.longPress === false) {
      this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes);
      this.clearArray(this.selectedNodes);
      if ($event.clientX > 200) {
        this.ngRedux.dispatch(this.actions.sendPaletteOpened(false));
      }
    }
  }

  clearArray(array: any[]): void {
    array.length = 0;
  }

  @HostListener('mousedown', ['$event'])
  showSelectionRange($event) {
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
  }

  @HostListener('mousemove', ['$event'])
  openSelector($event) {
    if (this.callOpenSelector) {
      this.selectionWidth = Math.abs(this.initialW - $event.pageX);
      this.selectionHeight = Math.abs(this.initialH - $event.pageY);
      if ($event.pageX <= this.initialW && $event.pageY >= this.initialH) {
        this.pageX = $event.pageX + this.offsetX;
      } else if ($event.pageY <= this.initialH && $event.pageX >= this.initialW) {
        this.pageY = $event.pageY - this.offsetY;
      } else if ($event.pageY < this.initialH && $event.pageX < this.initialW) {
        this.pageX = $event.pageX + this.offsetX;
        this.pageY = $event.pageY - this.offsetY;
      }
    }
  }

  @HostListener('mouseup', ['$event'])
  selectElements($event) {
    if (this.callSelectItems) {
      this.callOpenSelector = false;
      this.callSelectItems = false;
      for (const node of this.allNodeTemplates) {
        const aElem = document.getElementById('selection');
        const bElem = document.getElementById(node.id);
        const result = this.doObjectsCollide(aElem, bElem);
        if (result === true) {
          this.enhanceDragSelection(node.id);
        }
      }
      this.crosshair = false;
      this.selectionActive = false;
      this.selectionWidth = 0;
      this.selectionHeight = 0;
    }
  }

  private getOffset(el) {
    let _x = 0;
    let _y = 0;
    while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
      _x += el.offsetLeft - el.scrollLeft;
      _y += el.offsetTop - el.scrollTop;
      el = el.offsetParent;
    }
    return {top: _y, left: _x};
  }

  doObjectsCollide(a, b): boolean {
    const aTop = this.getOffset(a).top;
    const aLeft = this.getOffset(a).left;
    const bTop = this.getOffset(b).top;
    const bLeft = this.getOffset(b).left;

    return !(
      ((aTop + a.getBoundingClientRect().height) < (bTop)) ||
      (aTop > (bTop + b.getBoundingClientRect().height)) ||
      ((aLeft + a.getBoundingClientRect().width) < bLeft) ||
      (aLeft > (bLeft + b.getBoundingClientRect().width))
    );
  }

  repaintJsPlumb() {
    this.newJsPlumbInstance.repaintEverything();
  }

  ngDoCheck(): void {
    const pressedNavBarButton = this.differPressedNavBarButton.diff(this.pressedNavBarButton);

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
  }

  ngOnInit() {
    this.newJsPlumbInstance = this.jsPlumbService.getJsPlumbInstance();
    this.newJsPlumbInstance.setContainer('container');
    this.relationshipTemplates = this.jsonService.getRelationships();
    this.visuals = this.jsonService.getVisuals();
    this.assignVisuals();
  }

  assignVisuals() {
    this.visuals = this.jsonService.getVisuals();
    for (const node of this.allNodeTemplates) {
      for (const visual of this.visuals) {
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
  }

  makeDraggable($event): void {
    this.newJsPlumbInstance.draggable($event);
  }

  private checkingNodeSelectionForDuplicateIDs(id: string) {
    this.nodeSelected = false;
    for (const node of this.selectedNodes) {
      if (node === id) {
        this.nodeSelected = true;
      }
    }
    if (this.nodeSelected === false) {
      this.newJsPlumbInstance.removeFromAllPosses(this.selectedNodes);
      this.clearArray(this.selectedNodes);
    }
  }

  checkIfNodeInSelection($event): void {
    this.checkingNodeSelectionForDuplicateIDs($event);
  }

  arrayContainsNode(arrayOfNodes: any[], id: string): boolean {
    if (arrayOfNodes !== null && arrayOfNodes.length > 0) {
      for (let i = 0; i < arrayOfNodes.length; i++) {
        if (arrayOfNodes[i] === id) {
          return true;
        }
      }
    }
    return false;
  }

  private enhanceDragSelection(id: string) {
    this.nodeArrayEmpty = false;
    this.newJsPlumbInstance.addToPosse(id, 'dragSelection');
    this.nodeArrayEmpty = this.arrayContainsNode(this.selectedNodes, id);
    if (!this.nodeArrayEmpty) {
      this.selectedNodes.push(id);
    }
  }

  addNodeToDragSelection($event): void {
    this.enhanceDragSelection($event);
  }

  trackTimeOfMouseDown(e: Event): void {
    this.startTime = new Date().getTime();
  }

  trackTimeOfMouseUp(e: Event): void {
    this.endTime = new Date().getTime();
    this.testTimeDifference();
  }

  private testTimeDifference(): void {
    if ((this.endTime - this.startTime) < 250) {
      this.longPress = false;
    } else if (this.endTime - this.startTime >= 300) {
      this.longPress = true;
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
