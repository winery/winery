export class QName {
  private _localName: string;
  private _nameSpace: string;

  constructor(private _qName: string) {
  }

  get localName(): string {
    this._localName = this._qName.split('}')[1];
    return this._localName;
  }

  set localName(value: string) {
    this._localName = value;
  }

  get nameSpace(): string {
    this._nameSpace = this._qName.split('}')[0];
    return this._nameSpace + '}';
  }

  set nameSpace(value: string) {
    this._nameSpace = value;
  }
}
