import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'winery-modalFooter',
    templateUrl: 'winery.modal.footer.component.html'
})
export class WineryModalFooterComponent {

    @Input() showDefaultButtons = true;
    @Input() closeButtonLabel = 'Cancel';
    @Input() okButtonLabel = 'Add';
    @Input() modalRef: any;
    @Input() disableOkButton = false;
    @Output() onOk = new EventEmitter<any>();
    @Output() onCancel = new EventEmitter<any>();

    ok() {
        this.onOk.emit();
        this.modalRef.hide();
    }

    cancel() {
        this.onCancel.emit();
        this.modalRef.hide();
    }
}
