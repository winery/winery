import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'removeWhiteSpaces'
})

export class RemoveWhiteSpacesPipe implements PipeTransform {
    transform(value: any, args: any[]): string {
        if (value) {
            return value.toString().replace(/ /g, '');
        }
    }
}
