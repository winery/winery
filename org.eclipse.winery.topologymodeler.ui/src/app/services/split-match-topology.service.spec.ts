import { inject, TestBed } from '@angular/core/testing';

import { SplitMatchTopologyService } from './split-match-topology.service';

describe('SplitMatchTopologyService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [SplitMatchTopologyService]
        });
    });

    it('should be created', inject([SplitMatchTopologyService], (service: SplitMatchTopologyService) => {
        expect(service).toBeTruthy();
    }));
});
