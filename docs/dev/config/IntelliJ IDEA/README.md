# IntelliJ Configuration

Preparation: Generate a war to have all dependencies fetched by maven: `mvn package`

1. Install JRebel plugin
    - JRebel enables a better debugging - changes can be immediately loaded without building the whole project again
    - Download https://zeroturnaround.com/software/jrebel/
    - Get a JRebel license from <https://my.rebel.com>.
      It is for free if JRebel may post to your Twitter account.
    - File --> Settings --> Plugins --> Search for JRebel
    - If JRebel is not available press "Browse repositories" --> Search -->Install
2. Enable checkstyle: Follow the shown steps and apply them in IntelliJ
  ![Enable CheckStyle in IntelliJ](activate-checkstyle.gif)
  - Install the [IntelliJ Checkstyle Plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea).
  - Open Settings > Other Settings > CheckStyle.
  - Click on the green plus and add `checkstyle.xml` from the root of the Winery code repository.
3. Open `pom.xml` in the main directory
4. Configure the code style (Source: https://youtrack.jetbrains.com/issue/IDEA-61520#comment=27-1292600)
    1. Press <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>
    2. Please install CheckStyle-IDEA plugin (http://plugins.jetbrains.com/plugin/1065?pr=idea), it can be found via plug-in repository (Settings|Plugins|Browse repositories).
    3. Go to "Editor > Code Style"
    3. Click "Manage..." (right of "Scheme:")
    4. Click "Import..."
    4. Choose "CheckStyle Configuration"
    5. Navigate to  `checkstyle.xml`. It is located inside the winery git repository under the root folder.
    6. Press "OK"
    6. You will see a message "CheckStyle configuration settings were imported to ... scheme".
    7. Press "OK"
    8. Press "Close"
    9. Press "OK"
5. Setup code headers to be inserted automatically
    1. Open the Settings by pressing <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>
    2. Go to "Editor > File and Code Templates page"
    3. Edit each java, typescript, html and xml template to contain the license header shown in [CodeHeaders.md](../../CodeHeaders.md)
    4. Click apply
6. Setup tomcat as usual. In case you develop the backend, use following configuration:
  - `org.eclipse.winery.repository.rest:war exploded` to `/winery`
  - External artifact `.../org.eclipse.winery.repository.ui/target/winery-ui.war` to `/winery-ui`
  - `org.eclipse.winery.topologymodeler:war exploded` to `/winery-topologymodeler`
  - External artifact `.../org.eclipse.winery.workflowmodeler/target/winery-workflowmodeler.war` to `/winery-workflowmodeler`
7. Get a JetBrains account and vote up following issues (at the right side, just click the thumbs-up next to "Voters". In case you don't see "Voters", reload the page):
  - <https://youtrack.jetbrains.com/issue/IDEA-147601>
  - <https://youtrack.jetbrains.com/issue/IDEA-142591>
  - <https://youtrack.jetbrains.com/issue/IDEA-176611>
  - <https://youtrack.jetbrains.com/issue/IDEA-75437>
  - <https://youtrack.jetbrains.com/issue/IDEA-159739>
  - <https://youtrack.jetbrains.com/issue/IDEA-131223>
8. Pros: Install the [Key Promoter X plugin](https://plugins.jetbrains.com/plugin/9792-key-promoter-x).


## Further Remarks

* Please let `.editorconfig` override the settings of IntelliJ
* Shortcuts
  - 2x <kbd>Shift</kbd> / <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>F</kbd> / <kbd>Ctrl</kbd>+<kbd>F</kbd>: Differrent forms of search
  - <kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>L</kbd>: Organize imports (fixes checkstyle)
  - <kbd>Ctrl</kbd>+<kbd>X</kbd>: if nothing is marked: cut line (equal to marking whole line and using <kbd>Ctrl</kbd>+<kbd>X</kbd>)
  - <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>T</kbd>: Create/jump to the test class
* See [UpdateCopyrightHeaders](UpdateCopyrightHeaders.md) for updating old copyright headers

## License

Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
