import { Injectable, ViewContainerRef } from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';

@Injectable()
export class NotificationService {

    toastr: ToastsManager;
    notifications: Array<any> = [];

    constructor(pToastr: ToastsManager) {
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
    success(message: string, title = 'success') {
        this.toastr.success(message, title);
        this.notifications.push({title: title, message: message, type: 'success', createdOn: Date.now()});
    }

    /**
     *
     * @param message
     * @param title
     */
    error(message: string, title = 'error') {
        this.toastr.error(message, title);
        this.notifications.push({title: title, message: message, type: 'error', createdOn: Date.now()});
    }

    /**
     *
     * @param message
     * @param title
     */
    warning(message: string, title = 'warning') {
        this.toastr.warning(message, title);
        this.notifications.push({title: title, messages: message, type: 'warning', createdOn: Date.now()});
    }
}
