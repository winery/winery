import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

@Component({
    selector: 'modal-footer',
    templateUrl: 'winery.modal.footer.component.html'
})
export class WineryModalFooterComponent {

    @Input('show-default-buttons') showDefaultButtons: boolean = true;
    @Input('close-button-label') closeButtonLabe: string = 'Cancel';
    @Input('add-button-label') addButtonLabel: string = 'Add';
    @Input('modal-ref') modalRef: any;
    @Output() onAdd = new EventEmitter<any>();
    @Output() onCancel = new EventEmitter<any>();

    add() {
        this.onAdd.emit();
    }

    cancel() {
        this.onCancel.emit();
        this.modalRef.hide();
    }
}
