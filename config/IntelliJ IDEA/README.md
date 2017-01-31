# IntelliJ Configuration

1. First of all, generate a war to have all dependencies fetched by maven.
2. Open IntelliJ
3. Open `pom.xml` in the main directory
4. Configure the code style
    1. Press <kbd>Ctrl</kbd> + <kbd>Alt</kbd> + <kbd>S</kbd>
    2. Go to "Editor > Code Style"
    3. Click "Manage..." (right of "Scheme:")
    4. Click "Import..."
    5. Choose `IntelliJ Code Style.xml`
    6. Press "OK"
    7. Press "OK"
    8. Press "Close"
    9. Press "OK"
5. Setup tomcat as usual.
6. Recommended: Get a JRebel license from <https://my.rebel.com>.
   It is for free if JRebel may post to your Twitter account.

## Further Remarks

* Please let `.editorconfig` override the settings of IntelliJ
