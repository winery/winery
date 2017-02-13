import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'urlEncode'
})

export class UrlDecodePipe implements PipeTransform {
    transform(value: any, args: any[]): any {
        if (value) {
            return decodeURIComponent(value);
        }
    }
}
