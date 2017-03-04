import { Component, Input, AfterViewInit, ContentChild, AfterContentInit } from '@angular/core';
import { WineryModalHeaderComponent } from './winery.modal.header.component';
import { WineryModalFooterComponent } from './winery.modal.footer.component';
import { isNullOrUndefined } from 'util';

@Component({
    selector: 'winery-modal',
    templateUrl: 'winery.modal.component.html',
    host: {
        'class': 'modal fade',
        'role': 'dialog',
        'tabindex': '-1'
    }
})
export class WineryModalComponent implements AfterViewInit, AfterContentInit {

    @Input() modalRef: any;
    @Input() size: any;
    @Input() keyboard: boolean = true;
    @Input() backdrop: string | boolean = true;
    @ContentChild(WineryModalHeaderComponent) headerContent: WineryModalHeaderComponent;
    @ContentChild(WineryModalFooterComponent) footerContent: WineryModalFooterComponent;

    private overrideSize: string = null;
    private cssClass: string = '';

    ngAfterContentInit(): void {
        if (!isNullOrUndefined(this.headerContent)) {
            this.headerContent.modalRef = this.modalRef;
        }
        if (!isNullOrUndefined(this.footerContent)) {
            this.footerContent.modalRef = this.modalRef;
        }
    }

    ngAfterViewInit(): void {
        if (!this.backdrop) {
            this.modalRef.config.backdrop = 'static';
        } else {
            this.modalRef.config.backdrop = this.backdrop;
        }

        this.modalRef.config.keyboard = this.keyboard;

        if (ModalSize.validSize(this.size)) this.overrideSize = this.size;
    }

    getCssClasses(): string {
        let classes: string[] = [];

        if (this.isSmall()) {
            classes.push('modal-sm');
        }

        if (this.isLarge()) {
            classes.push('modal-lg');
        }

        if (this.cssClass !== '') {
            classes.push(this.cssClass);
        }

        return classes.join(' ');
    }

    private isSmall() {
        return this.overrideSize !== ModalSize.LARGE
            && this.size === ModalSize.SMALL
            || this.overrideSize === ModalSize.SMALL;
    }

    private isLarge() {
        return this.overrideSize !== ModalSize.SMALL
            && this.size === ModalSize.LARGE
            || this.overrideSize === ModalSize.LARGE;
    }
}

export class ModalSize {
    static SMALL = 'sm';
    static LARGE = 'lg';

    static validSize(size: string) {
        return size && (size === ModalSize.SMALL || size === ModalSize.LARGE);
    }

}
