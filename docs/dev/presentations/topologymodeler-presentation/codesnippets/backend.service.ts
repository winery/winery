@Injectable()
export class BackendService {

    readonly headers = new HttpHeaders().set('Accept', 'application/json');

    configuration: TopologyModelerConfiguration;
    serviceTemplateURL: string;
    serviceTemplateUiUrl: string;

    endpointConfiguration = new Subject<any>();
    endpointConfiguration$ = this.endpointConfiguration.asObservable();

    private allEntities = new Subject<any>();
    allEntities$ = this.allEntities.asObservable();

    constructor(private http: HttpClient,
                private alert: ToastrService,
                private errorHandler: ErrorHandlerService) {
        this.endpointConfiguration$.subscribe((params: TopologyModelerConfiguration) => {
            if (!(isNullOrUndefined(params.id) && isNullOrUndefined(params.ns) &&
                isNullOrUndefined(params.repositoryURL) && isNullOrUndefined(params.uiURL))) {

                this.configuration = new TopologyModelerConfiguration(
                    params.id,
                    params.ns,
                    params.repositoryURL,
                    params.uiURL,
                    params.compareTo,
                    params.compareTo ? true : params.isReadonly,
                    params.parentPath,
                    params.elementPath
                );

                const url = this.configuration.parentPath + '/'
                    + encodeURIComponent(encodeURIComponent(this.configuration.ns)) + '/'
                    + this.configuration.id;
                this.serviceTemplateURL = this.configuration.repositoryURL + '/' + url;
                this.serviceTemplateUiUrl = this.configuration.uiURL + url;

                // All Entity types
                this.requestAllEntitiesAtOnce().subscribe(data => {
                    // add JSON to Promise, WineryComponent will subscribe to its Observable
                    this.allEntities.next(data);
                });
            }
        });
    }

    /**
     * Requests all entities together.
     * We use forkJoin() to await all responses from the backend.
     * This is required
     * @returns data  The JSON from the server
     */
    private requestAllEntitiesAtOnce(): Observable<any> {
        if (this.configuration) {
            return forkJoin(
                this.requestGroupedNodeTypes(),
                this.requestArtifactTemplates(),
                this.requestTopologyTemplateAndVisuals(),
                this.requestArtifactTypes(),
                this.requestPolicyTypes(),
                this.requestCapabilityTypes(),
                this.requestRequirementTypes(),
                this.requestPolicyTemplates(),
                this.requestRelationshipTypes(),
                this.requestNodeTypes()
            );
        }
    }

    /**
     * Requests all grouped node types from the backend
     * @returns {Observable<string>}
     */
    private requestGroupedNodeTypes(): Observable<any> {
        if (this.configuration) {
            return this.http.get(
                backendBaseURL + '/nodetypes?grouped&full',
                { headers: this.headers }
            );
        }
    }
}