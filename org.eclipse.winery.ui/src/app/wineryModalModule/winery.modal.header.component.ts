import { Component, Input } from '@angular/core';

@Component({
    selector: 'winery-modalHeader',
    templateUrl: 'winery.modal.header.component.html'
})
export class WineryModalHeaderComponent {
    @Input() modalRef: any;
    @Input() title: string = '';
}
