import { Component, Input } from '@angular/core';

@Component({
    selector: 'winery-modal-header',
    templateUrl: 'winery.modal.header.component.html'
})
export class WineryModalHeaderComponent {
    @Input() modalRef: any;
    @Input() title = '';
}
