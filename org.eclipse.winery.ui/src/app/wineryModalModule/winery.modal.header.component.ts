import { Component, Input } from '@angular/core';

@Component({
    selector: 'modal-header',
    templateUrl: 'winery.modal.header.component.html'
})
export class WineryModalHeaderComponent {
    @Input('modal-ref') modalRef: any;
    @Input() title: string = '';
}
