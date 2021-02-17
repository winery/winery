import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BackendService } from './backend.service';
import { Observable } from 'rxjs';

@Injectable()
export class MultiParticipantsService {

    private readonly httpHeaders: HttpHeaders;

    constructor(private httpClient: HttpClient,
                private backendService: BackendService) {
        this.httpHeaders = new HttpHeaders().set('Content-Type', 'application/json');
    }

    postNewVersion(): Observable<any> {
        const url = this.backendService.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
            + '/'
            + encodeURIComponent(this.backendService.configuration.id)
            + '/createplaceholderversion';

        return this.httpClient.post(
            url,
            { headers: this.httpHeaders, observe: 'response', responseType: 'text' });
    }

    postParticipantsVersion(): Observable<any> {
        const url = this.backendService.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
            + '/'
            + encodeURIComponent(this.backendService.configuration.id)
            + '/createparticipantsversion';
        return this.httpClient.post(
            url,
            { headers: this.httpHeaders, observe: 'response', responseType: 'json' }
        );
    }

    postSubstituteVersion(): Observable<any> {
        const url = this.backendService.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
            + '/'
            + encodeURIComponent(this.backendService.configuration.id)
            + '/placeholdersubstitution';
        console.log(url);
        return this.httpClient.post(
            url,
            { headers: this.httpHeaders, observe: 'response', responseType: 'json' }
        );
    }

    postPlaceholders(serviceTemplateId: string): Observable<any> {
        const url = this.backendService.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
            + '/'
            + encodeURIComponent(serviceTemplateId)
            + '/placeholder/generator';
        return this.httpClient.post(
            url,
            { headers: this.httpHeaders, observe: 'response', responseType: 'string' });
    }
}
