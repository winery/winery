import { Component, Input } from '@angular/core';

@Component({
    selector: 'winery-entity-container',
    templateUrl: 'entityContainer.component.html',
    inputs: [
        'componentId',
        'namespace',
        'resourceType'
    ]
})
export class EntityContainerComponent {
    @Input() componentId: string;
    @Input() namespace: string;
    @Input() resourceType: string;
}
