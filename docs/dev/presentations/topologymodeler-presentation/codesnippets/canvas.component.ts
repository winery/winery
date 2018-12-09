constructor(private jsPlumbService: JsPlumbService,
            private eref: ElementRef,
            private layoutDirective: LayoutDirective,
            private ngRedux: NgRedux<IWineryState>,
            private actions: WineryActions,
            private topologyRendererActions: TopologyRendererActions,
            private zone: NgZone,
            private hotkeysService: HotkeysService,
            private renderer: Renderer2,
            private alert: ToastrService,
            private differs: KeyValueDiffers,
            private backendService: BackendService,
            private importTopologyService: ImportTopologyService,
            private existsService: ExistsService,
            private splitMatchService: SplitMatchTopologyService,
            private reqCapService: ReqCapService,
            private errorHandler: ErrorHandlerService) {
    this.newJsPlumbInstance = this.jsPlumbService.getJsPlumbInstance();
    this.newJsPlumbInstance.setContainer('container');

    this.subscriptions
        .push(this.ngRedux
        .select(state => state.wineryState.currentJsonTopology.nodeTemplates)
        .subscribe(currentNodes => this.updateNodes(currentNodes)));
    this.subscriptions
        .push(this.ngRedux
        .select(state => state.wineryState.currentJsonTopology.relationshipTemplates)
        .subscribe(currentRelationships => this.updateRelationships(currentRelationships)));
    this.subscriptions
        .push(this.ngRedux
        .select(state => state.topologyRendererState)
        .subscribe(currentButtonsState => this.setRendererState(currentButtonsState)));
    this.subscriptions
        .push(this.ngRedux
        .select(state => state.wineryState.currentNodeData)
        .subscribe(currentNodeData => this.toggleMarkNode(currentNodeData)));
    this.gridTemplate = new GridTemplate(100, false, false, 30);
    this.subscriptions
        .push(this.ngRedux
        .select(state => state.wineryState.currentPaletteOpenedState)
        .subscribe(currentPaletteOpened => this.setPaletteState(currentPaletteOpened)));
    this.hotkeysService
        .add(new Hotkey('mod+a', (event: KeyboardEvent): boolean => {
            event.stopPropagation();
            this.allNodeTemplates.forEach(node => this.enhanceDragSelection(node.id));
            return false; // Prevent bubbling
    }, undefined, 'Select all Node Templates'));
    this.hotkeysService.add(new Hotkey('del', (event: KeyboardEvent): boolean => {
        this.handleDeleteKeyEvent();
        return false;
    }, undefined, 'Delete an element.'));
    this.capabilities = new CapabilitiesModalData();
    this.requirements = new RequirementsModalData();
    this.importTopologyData = new ImportTopologyModalData();
}
