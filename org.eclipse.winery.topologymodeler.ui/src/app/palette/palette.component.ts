import {Component, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { PaletteService } from '../palette.service';
import {AppActions} from '../redux/actions/app.actions';
import {NgRedux} from '@angular-redux/store';
import {IAppState} from '../redux/store/app.store';
import {TNodeTemplate} from '../ttopology-template';

@Component({
  selector: 'app-palette-component',
  templateUrl: './palette.component.html',
  styleUrls: ['./palette.component.css'],
  providers: [PaletteService],
  animations: [
    trigger('paletteRootState', [
      state('shrunk', style({
        height: '100px',
        width: '40px',
      })),
      state('extended', style({
        height: '40px',
        width: '100%',
      })),
      transition('shrunk => extended', animate('200ms ease-out')),
      transition('extended => shrunk', animate('200ms ease-out'))
    ]),

    trigger('paletteRootTextState', [
      state('shrunk', style({
        transform: 'rotate(270deg)',
        textAlign: 'center',
        marginTop: '40px',
      })),
      state('extended', style({
        textAlign: 'center',
        marginTop: '0px',
        transform: 'rotate(360deg)',
      })),
      transition('shrunk => extended', animate('200ms ease-out')),
      transition('extended => shrunk', animate('200ms ease-out'))
    ]),

    trigger('paletteItemState', [
      state('shrunk', style({
        display: 'none',
        opacity: '0',
        width: '40px',
      })),
      state('extended', style({
        display: 'block',
        opacity: '1',
        width: '100%',
      })),
      transition('shrunk => extended', animate('200ms ease-out')),
      transition('extended => shrunk', animate('200ms ease-out'))
    ])
  ]
})
export class PaletteComponent implements OnInit, OnDestroy {
  detailsAreHidden = true;
  paletteRootState = 'shrunk';
  paletteItems = [];
  allNodeTemplates: Array<TNodeTemplate> = [];
  subscription;

  constructor(private paletteService: PaletteService,
              private ngRedux: NgRedux<IAppState>,
              private actions: AppActions) {
    this.subscription = ngRedux.select<any>('appState')
      .subscribe(newState => {
        this.updateState(newState.currentPaletteOpenedState);
        this.addNodes(newState.currentSavedJsonTopology.nodeTemplates);
      });
    this.paletteItems = paletteService.getPaletteData();
  }

  updateState(newPaletteOpenedState: any) {
    if (!newPaletteOpenedState) {
      this.paletteRootState = 'shrunk';
    }
  }

  addNodes(nodeTemplates: Array<TNodeTemplate>) {
    if (nodeTemplates.length > 0) {
      if (this.allNodeTemplates.length === 0) {
        this.allNodeTemplates = nodeTemplates;
      }
      this.checkNodes(nodeTemplates);
    }
  }

  checkNodes(currentNodes: Array<TNodeTemplate>) {
    if (currentNodes !== null) {
      const newNode = currentNodes[currentNodes.length - 1];
      if (this.allNodeTemplates.length !== 0) {
        const lastNodeId = this.allNodeTemplates[this.allNodeTemplates.length - 1].id;
        const newNodeId = newNode.id;
        if (lastNodeId !== newNodeId) {
          this.allNodeTemplates.push(newNode);
        }
      } else {
        this.allNodeTemplates.push(newNode);
      }
    }
  }

  ngOnInit() {
  }

  public openPalette(): void {
    this.detailsAreHidden = false;
    this.toggleRootState();

  }

  public toggleRootState(): void {
    if (this.paletteRootState === 'shrunk') {
      this.paletteRootState = 'extended';
      this.ngRedux.dispatch(this.actions.sendPaletteOpened(true));
    } else {
      this.paletteRootState = 'shrunk';
      this.ngRedux.dispatch(this.actions.sendPaletteOpened(true));
    }
  }

  publishTitle($event): void {
    const left = ($event.pageX - 100).toString();
    const top = ($event.pageY - 30).toString();
    const name = $event.target.innerHTML;
    const otherAttributes = {
        location: 'undefined',
        x: left,
        y: top
    };
    const newId = this.generateId(name);
    const paletteItem: TNodeTemplate = new TNodeTemplate(
      undefined,
      newId,
      undefined,
      name,
      1,
      1,
      undefined,
      undefined,
      undefined,
      undefined,
      otherAttributes
    );
   this.ngRedux.dispatch(this.actions.saveNodeTemplate(paletteItem));
  }

  generateId(name: string): string {
    if (this.allNodeTemplates.length > 0) {
      for (let i = this.allNodeTemplates.length - 1; i >= 0; i--) {
        if (name === this.allNodeTemplates[i].name) {
          const idOfCurrentNode = this.allNodeTemplates[i].id;
          const numberOfNewInstance = parseInt(idOfCurrentNode.substring(idOfCurrentNode.indexOf('_') + 1), 10) + 1;
          if (numberOfNewInstance) {
            const newId = name.concat('_', numberOfNewInstance.toString());
            return newId;
          } else {
            const newId = name.concat('_', '2');
            return newId;
          }
        }
      }
        return name;
    } else {
      return name;
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}


