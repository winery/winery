# Update Copyright Header

Steps to update copyright headers:
1. Define scopes to apply the copy right header to:
    - Open properties <kbd>Ctrl</kbd> <kbd>Alt</kbd> <kbd>S</kbd> and search for `Scopes`
    - Select packages for the scope and click `Include Recursively`
    ![](figures/SetScopes.png)
2. Apply copyright settings to Scope:
    - Open properties `ctrl alt s` and search for `Copyright`
    - Add copyright entry and apply previously created scope
    ![](figures/Copyright.png)
3. Create copyright profile:
    - Open Settings/Editor/Copyright/'Copyright Profiles'
    - Add copyright text without borders
        - Set Copyright year dynamically with `${today.year}` 
    - Add copyright regex (Acquired by selecting the copyright header in intellij editor and pressing `ctrl shift f`)
    - Allow replacing old copyright identified by regex seems not to work
    ![](figures/CopyrightProfiles.png)
4. Adjust copyright formatting settings
    - Open Settings/Editor/Copyright/Formatting
    - Change to `Use block comments` with `Prefix each line`, set `Relative Location` to `Before other comments`and increase `Separator before/after Length` to `81`
    ![](figures/CopyrightFormat.png)
5. Delete previous copyright header manually (Replace with empty String)
6. Right click package and choose `Update Copyright..`
    - Check files for duplicated copyright header (occurs if copyright regex not set correctly)
