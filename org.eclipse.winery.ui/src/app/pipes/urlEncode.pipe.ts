import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'urlEncode'
})

export class UrlEncodePipe implements PipeTransform {
    transform(value: any, args: any[]): any {
        if (value) {
            return encodeURIComponent(value);
        }
    }
}
