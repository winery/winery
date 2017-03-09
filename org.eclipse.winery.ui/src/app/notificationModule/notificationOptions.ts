import { ToastOptions } from 'ng2-toastr';

/**
 * this class can be used to override the default setting of ng2-toastr
 *
 * @Class
 */
export class CustomOption extends ToastOptions {
    animate = 'flyRight'; // you can override any options available
    newestOnTop = false;
    showCloseButton = true;
}
