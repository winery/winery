import { Component, Input } from '@angular/core';

@Component({
    selector: 'winery-entity-container',
    templateUrl: 'entityContainer.component.html',
    inputs: [
        'componentName',
        'componentId',
        'namespace',
        'resourceType'
    ]
})
export class EntityContainerComponent {
    @Input() componentName: string;
    @Input() componentId: string;
    @Input() namespace: string;
    @Input() resourceType: string;
}
