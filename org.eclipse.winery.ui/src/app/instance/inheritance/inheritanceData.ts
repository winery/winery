export class InheritanceData {
    isAbstract: string;
    isFinal: string;
    derivedFrom?: string;
    availableSuperClasses: [
        { name: string, qName: string }
        ];
}
