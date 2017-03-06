import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'modal-footer',
    templateUrl: 'winery.modal.footer.component.html'
})
export class WineryModalFooterComponent {

    @Input('show-default-buttons') showDefaultButtons: boolean = true;
    @Input('close-button-label') closeButtonLabel: string = 'Cancel';
    @Input('ok-button-label') okButtonLabel: string = 'Add';
    @Input('modal-ref') modalRef: any;
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
