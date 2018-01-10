## Update copyright header

Steps to update copyright headers:
1. Define scopes to apply the copy right header to:
    - Open properties `ctrl alt s` and search for `Scopes`
    - Select packages for the scope and click `Include Recursively`
    ![GitAutoCheck](graphics/SetScopes.png)
2. Apply copyright settings to Scope:
    - Open properties `ctrl alt s` and search for `Copyright`
    - Add copyright entry and apply previously created scope
    ![GitAutoCheck](graphics/Copyright.png)
3. Create copyright profile:
    - Open Settings/Editor/Copyright/'Copyright Profiles'
    - Add copyright text without borders
        - Set Copyright year dynamically with `${today.year}` 
    - Add copyright regex (Acquired by selecting the copyright header in intellij editor and pressing `ctrl shift f`)
    - Allow replacing old copyright identified by regex seems not to work
    ![GitAutoCheck](graphics/CopyrightProfiles.png)
4. Adjust copyright formatting settings
    - Open Settings/Editor/Copyright/Formatting
    - Change to `Use block comments` with `Prefix each line`, set `Relative Location` to `Before other comments`and increase `Separator before/after Length` to `81`
    ![GitAutoCheck](graphics/CopyrightFormat.png)
5. Delete previous copyright header manually (Replace with empty String)
6. Right click package and choose `Update Copyright..`
    - Check files for duplicated copyright header (occurs if copyright regex not set correctly)

## License

Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
