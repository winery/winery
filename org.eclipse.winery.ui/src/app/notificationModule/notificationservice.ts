import { Injectable, ViewContainerRef } from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { DatePipe } from '@angular/common';

@Injectable()
export class NotificationService {

    toastr: ToastsManager;
    notifications: Array<any> = [];

    constructor(pToastr: ToastsManager,
                private datePipe: DatePipe) {
        this.toastr = pToastr;
    }

    /**
     * Initializes the Notification Service
     * Important: this function must be called before using the the service
     *
     * @param rootVcr - View Container Reference of the root component
     */
    init(rootVcr: ViewContainerRef) {
        this.toastr.setRootViewContainerRef(rootVcr);

    }

    /**
     * returns a List of all previously created notifications
     * @returns {Array<any>} - contains the notification objects
     */
    getHistory(): Array<any> {
        return this.notifications;
    }

    /**
     *
     * @param message
     * @param title
     */
    success(message: string, title: string = 'success') {
        this.toastr.success(message, title);
        this.notifications.push({title: title, message: message, type: 'success', createdOn: this.getCurrentDate()});
    }

    /**
     *
     * @param message
     * @param title
     */
    error(message: string, title: string = 'error') {
        this.toastr.error(message, title);
        this.notifications.push({title: title, message: message, type: 'error', createdOn: this.getCurrentDate()});
    }

    /**
     *
     * @param message
     * @param title
     */
    warning(message: string, title: string = 'warning') {
        this.toastr.warning(message, title);
        this.notifications.push({title: title, messages: message, type: 'warning', createdOn: this.getCurrentDate()});
    }

    /**
     * returns the current date
     * @returns {string}
     */
    getCurrentDate() {
        return this.datePipe.transform(Date.now(), 'short');
    }
}
