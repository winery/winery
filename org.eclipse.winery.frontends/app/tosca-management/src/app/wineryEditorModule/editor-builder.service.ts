import { Injectable } from '@angular/core';



@Injectable({
    providedIn: 'root'
})
export class EditorBuilderService {

    defaultPluginURLs = [
        'assets/built-codeEdit15_1/javascript/plugins/javascriptPlugin.html',
        'assets/built-codeEdit15_1/webtools/plugins/webToolsPlugin.html',
        'assets/built-codeEdit15_1/plugins/embeddedToolingPlugin.html'
    ];

    userPluginURLs: any[] = [];
    editorBuilder: any;

    constructor() {
        this.editorBuilder = new orion.codeEdit({
            _defaultPlugins: this.defaultPluginURLs, userPlugins: this.userPluginURLs
        });
    }

    createEditor(parent: string) {
        return this.editorBuilder.create({
            parent: parent
        });
    }
}
