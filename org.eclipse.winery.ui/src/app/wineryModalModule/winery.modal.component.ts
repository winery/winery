import { AfterContentInit, AfterViewInit, Component, ContentChild, HostBinding, Input } from '@angular/core';
import { isNullOrUndefined } from 'util';
import { WineryModalFooterComponent } from './winery.modal.footer.component';
import { WineryModalHeaderComponent } from './winery.modal.header.component';

/**
 * This component provides a generic modal component for any kind of pop-ups.
 * To use it, the {@link WineryModalModule} must be imported in the corresponding module.
 *
 * In order to use this component, see the following example, note that the <code>modalRef</code> must be set.
 * For further information, see the sub-components {@link WineryModalHeaderComponent}, {@link WineryModalBodyComponent},
 * and {@link WineryModalFooterComponent}.
 *
 * @example <caption>Short Example</caption>
 * ```html
 * <winery-modal bsModal #confirmDeleteModal="bs-modal" [modalRef]="confirmDeleteModal">
 *     <winery-modalHeader>
 *         <h4 class="modal-title">Delete Property</h4>
 *     </winery-modalHeader>
 *     <winery-modalBody>
 *         <p *ngIf="elementToRemove != null">
 *             Do you want to delete the Element
 *                 <span style="font-weight:bold;">
 *                     {{ elementToRemove.key }}
 *                 </span>
 *             ?
 *         </p>
 *     </winery-modalBody>
 *     <winery-modalFooter (onOk)="removeConfirmed();"
 *                   [close-button-label]="'No'"
 *                   [ok-button-label]="'Yes'">
 *     </winery-modalFooter>
 * </winery-modal>
 * ```
 */
@Component({
    selector: 'winery-modal',
    templateUrl: 'winery.modal.component.html',
})
export class WineryModalComponent implements AfterViewInit, AfterContentInit {

    /**
     * The modalRef must be set, otherwise the component will not work!
     *
     * @Input
     */
    @Input() modalRef: any;
    /**
     * @Input
     */
    @Input() size: any;
    /**
     * @Input
     * @type {boolean}
     */
    @Input() keyboard = true;
    /**
     * @Input
     * @type {boolean}
     */
    @Input() backdrop: string | boolean = true;

    @HostBinding('class') hostClass = 'modal fade';
    @HostBinding('attr.role') hostRole = 'dialog';
    @HostBinding('tabindex') hostTabIndex = '-1';

    @ContentChild(WineryModalHeaderComponent) headerContent: WineryModalHeaderComponent;
    @ContentChild(WineryModalFooterComponent) footerContent: WineryModalFooterComponent;

    private overrideSize: string = null;
    private cssClass = '';

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

        if (ModalSize.validSize(this.size)) {
            this.overrideSize = this.size;
        }
    }

    getCssClasses(): string {
        const classes: string[] = [];

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

/**
 * This class is used to determine the modal's size
 */
export class ModalSize {
    static SMALL = 'sm';
    static LARGE = 'lg';

    static validSize(size: string) {
        return size && (size === ModalSize.SMALL || size === ModalSize.LARGE);
    }

}
