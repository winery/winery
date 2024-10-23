import * as vscode from 'vscode';
import * as path from 'path';
import { execSync } from 'child_process';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient/node';
export function activate(context: vscode.ExtensionContext) {
    const logChannel = vscode.window.createOutputChannel("TOSCA_LSP");
    logChannel.appendLine("Starting the TOSCA Language Server Extension.");
    getJavaHome().then(javaHome => {
        const executable = path.join(javaHome, 'bin', 'java');

        const classPath = path.join('C:', 'Users', 'LAPTOP', 'Documents', 'GitHub', 'winery', 'org.eclipse.winery.lsp', 'build', 'libs', 'org.eclipse.winery.lsp-1.0-SNAPSHOT.jar');        
        const args = ['-cp', classPath, 'org.eclipse.winery.lsp.Launcher.StdioLauncher'];

        logChannel.appendLine(`Java Executable: ${executable}`);
        logChannel.appendLine(`Classpath: ${classPath}`);
        logChannel.appendLine(`Args: ${args.join(' ')}`);

        const serverOptions: ServerOptions = {
            command: executable,
            args,
            options: {}
        };

        const clientOptions: LanguageClientOptions = {
            documentSelector: [{ scheme: 'file', language: 'yaml' }],
            synchronize: {
                fileEvents: vscode.workspace.createFileSystemWatcher('**/*.yaml')
            },
            outputChannel: logChannel
        };

        const client = new LanguageClient('tosca-lang-client', 'TOSCA Language Server', serverOptions, clientOptions);
        const disposable = client.start();
        context.subscriptions.push(disposable);
    }).catch(err => {
        logChannel.appendLine('Error finding Java home: ' + err);
    });
}

export function deactivate() {} 

function getJavaHome(): Promise<string> {
    const cmd = process.platform === 'win32' ?
        'java -XshowSettings:properties -version 2>&1 | findstr "java.home"' :
        "java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home'";

    return new Promise((resolve, reject) => {
        try {
            const output = execSync(cmd).toString();
            const javaHome = output.split('java.home =')[1].trim();
            resolve(javaHome);
        } catch (error) {
            reject(error);
        }
    });
}
