<!---~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2020 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->


# IntelliJ Configuration

## Preparation: Install IntelliJ

1. Get a JetBrains Ultimate License. For students: Visit <https://www.jetbrains.com/student>.
1. Install [JetBrains Toolbox](https://www.jetbrains.com/toolbox/): `choco install jetbrainstoolbox` and `choco pin add -n jetbrainstoolbox`, because JetBrains does an auto update
1. Install "IntelliJ IDEA Ultimate" using the JetBrains Toolbox.

## Preparation: Build Winery

Build Winery to have all dependencies fetched by Maven: `mvn clean install -DskipTests`.

## Setup IntelliJ

1. At start of IntelliJ, browse to the root `pom.xml` and open it as project.
1. Enable checkstyle: Follow the shown steps and apply them in IntelliJ
   ![](figures/activate-checkstyle.gif)
   - Install the [IntelliJ Checkstyle Plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea).
     It can be found via plug-in repository (Settings -> Plugins -> Browse repositories)
   - Open the Settings (by pressing <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>)
   - Go to Other Settings -> CheckStyle.
   - Click on the green plus and add `checkstyle.xml` from the root of the Winery code repository.
1. Configure the code style
    1. Open the Settings (by pressing <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>)
    1. Go to "Editor > Code Style"
    1. Click "Manage..." (right of "Scheme:")
    1. Click "Import Scheme"
    1. Choose "IntelliJ IDEA code style XML"
    1. Navigate to `intellij-idea-code-style.xml`. It is located in `docs/config/IntelliJ IDEA`.
    1. Press "OK"
    1. You will see a message "Winery configuration settings were imported".
    1. Press "OK"
    1. Press "Close"
    1. Press "OK"
1. Setup code headers to be inserted automatically
    1. Open the Settings (by pressing <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>)
    1. Go to Editor > Copyright  > Copyright Profiles
    1. Click the green plus
    1. Name "Winery"
    1. Copyright text from [Source Code Headers](../../dev/source-code-headers.md)
    1. Go to Editor > Copyright > Formatting
    1. Adjust copyright formatting settings
       - Change to `Use block comments` with `Prefix each line`
       - Set `Relative Location` to `Before other comments`
       - Increase `Separator before/after Length` to `81`
       - ![GitAutoCheck](figures/CopyrightFormat.png)
    1. Go to Editor > Copyright
    1. Set "Winery" as Default project copyright
    1. Press "OK"
1. Setup Apache Tomcat
    1. Download Tomcat 9.0 from <https://tomcat.apache.org/download-90.cgi>.
       Choose "zip" (e.g., <http://mirror.synyx.de/apache/tomcat/tomcat-9/v9.0.7/bin/apache-tomcat-9.0.7.zip>).
    1. Extract it to `c:\apache`. Result: `C:\apache\apache-tomcat-9.0.7`.
    1. ![](figures/run-step1-edit-configuration.png)
    1. ![](figures/run-step2-add-new-configuration.png)
    1. ![](figures/run-step3-add-tomcat.png)
    1. ![](figures/run-step4-configure-tomcat-button.png)
    1. ![](figures/run-step5-add-appserver-button.png.png)
    1. ![](figures/run-step6-set-apache-tomcat-directory.png)
    1. Press OK <br>
       ![](figures/run-step7-confirm.png)
    1. Set name and no browser launch:<br>
       ![](figures/run-step8-name-and-no-browser.png)
    1. Add deployment artifacts:<br>
       ![](figures/run-step9-button-add-deployment-artifacts.png)
    1. ![](figures/run-step10-choose-artifact.png)
    1. ![](figures/run-step11-select-artifcat.png)
    1. Set `/winery` as application context:<br>
       ![](figures/run-step12-set-winery-as-application-context.png)
    1. Click "Apply"
1. Setup npm run for TOSCA Management UI
    1. ![](figures/run-repositoryui-step1-add-npm-config.png)
    1. ![](figures/run-repositoryui-step2-configure.png)
1. Setup npm run for Topology Modeler
    1. ![](figures/run-topologymodeler-step1-add-npm-config.png)
    1. ![](figures/run-topologymodeler-step2-configure.png)
1. Setup npm run for Workflowmodeler (BPMN4TOSCA Modeler)
    1. ![](figures/run-topologymodeler-step1-add-npm-config.png)
    1. ![](figures/run-workflowmodeler-step2.png)
1. Optional: Setup XSD validation for TOSCA files
    1. Open the Settings (by pressing <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>)
    1. Go to "Languages & Frameworks"
    1. Select "Schemas and DTDs"
    1. Click on the plus on the right at "External Schemas and DTDs"
    1. Enter `http://docs.oasis-open.org/tosca/ns/2011/12` as URI
    1. In "Project Schemas" search for `TOSCA-v1.0.xsd`.
       It should be located at "org.eclipse.winery.common".  
       In the case of opening `C:\winery-repository` in IntelliJ, you have to select enter `C:\git-repositories\winery\org.eclipse.winery.common\src\main\resources\TOSCA-v1.0.xsd` in the Field "File"
    1. Click "OK".
    1. Go to "Editor"
    1. Select "File Types"
    1. At "Recognized File Types", scroll down to XML
    1. Select "XML"
    1. At "Registered Patterns", click on the Plus
    1. Enter `*.tosca` in the popup.
    1. Click "OK"
    1. Click "OK"
1. Optional: In case you do not want to have the live-update of Angular, you build the WARs of the UI and then deploy as follows:
    - External artifact `.../org.eclipse.winery.frontends/target/tosca-management.war` to `/`
    - External artifact `.../org.eclipse.winery.frontends/target/topologymodeler.war` to `/winery-topology-modeler`
    - External artifact `.../org.eclipse.winery.frontends/target/workflowmodeler.war` to `/winery-workflowmodeler`
1. Run everything
   - Select "Winery - REST". Click on "Play" (the green rectangle)
   - Select "TOSCA Mangement UI". Click on "Play"
   - Select "Topolgoy Modeler". Click on "Play".
   - Open <http://localhost:4200> in your browser.
1. Demonstration: Open winery-repository (AKA tosca-definitions) in IntelliJ  
   This ensures that you can work with the TOSCA files using the IDE.
   1. Go to File -> Open...
   1. Enter `c:\winery-repository`
   1. Click "OK"
   1. At the dialog "Open Project" choose "New Window"
   1. Expand `winery-repository` (on the left side)
   1. Expand `nodetypes`
   1. Expand `http%3A%2F%2Fplain.winery.opentosca.org%2Fnodetypes`
   1. Expand `NodeTypeWithTwoKVProperties`
   1. Double click `NodeType.tosca`
   1. Go to line 14
   1. Type in `<`
   1. See that `<tosca:CapabilityDefinitions` and other `tosca` elements are proposed
   1. No further action required. You can close the window.

## Further Remarks

* Please let `.editorconfig` override the settings of IntelliJ
* Shortcuts
  - 2x <kbd>Shift</kbd> / <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>F</kbd> / <kbd>Ctrl</kbd>+<kbd>F</kbd>: Differrent forms of search
  - <kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>L</kbd>: Organize imports (fixes checkstyle)
  - <kbd>Ctrl</kbd>+<kbd>X</kbd>: if nothing is marked: cut line (equal to marking whole line and using <kbd>Ctrl</kbd>+<kbd>X</kbd>)
  - <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>T</kbd>: Create/jump to the test class
* See [Update Copyright Header](copyright-header.md) for updating old copyright headers
