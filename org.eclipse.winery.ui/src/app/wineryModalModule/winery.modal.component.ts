import {
    Component, OnInit, Input, ElementRef, ViewChild, AfterViewInit, ContentChild,
    AfterContentInit
} from '@angular/core';
import { WineryModalHeaderComponent } from './winery.modal.header.component';
import { WineryModalFooterComponent } from './winery.modal.footer.component';

@Component({
    selector: 'winery-modal-component',
    templateUrl: 'winery.modal.component.html',
    host: {
        'class': 'modal fade',
        'role': 'dialog',
        'tabindex': '-1'
    }
})
export class WineryModalComponent implements OnInit, AfterViewInit, AfterContentInit {


    @Input() modalRef: any;
    @Input() size: any;
    @Input() keyboard: boolean = true;
    @Input() backdrop: string | boolean = true;
    @ContentChild(WineryModalHeaderComponent) headerContent: any;
    @ContentChild(WineryModalFooterComponent) footerContent: any;

    private overrideSize: string = null;
    private cssClass: string = '';

    constructor() {

    }

    ngOnInit() {

    }

    ngAfterContentInit(): void {
        console.log(this.headerContent);
        console.log(this.footerContent);

        this.footerContent.modalRef = this.modalRef;
    }

    ngAfterViewInit(): void {
        console.log(this.modalRef.config);
        if (!this.backdrop) {
            this.modalRef.config.backdrop = 'static';
        }else {
            this.modalRef.config.backdrop = this.backdrop;
        }

        this.modalRef.config.keyboard = this.keyboard;
        console.log(this.modalRef.config);
        console.log('size= ' + this.size);

        if (ModalSize.validSize(this.size)) this.overrideSize = this.size;

        console.log('size= ' + this.size);

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
