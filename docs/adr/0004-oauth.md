<!--
Contributors:
    Lukas Harzenetter - API and implementation
-->

# OAuth with GitHub
**UserStory:**

In order to use (private) repositories instead of uploading all files into a Artifact Template, it is necessary to log in into GitHub.
  Therefore, the OAuth flow is implented as follows:
  
1. By clicking the `Login with GitHub` button, the browser gets redirected to GitHub for authentication.
    1. Login and authorize the Application to access the private repositories.
    1. Or, if the application was already been authorized, GitHub automatically continues with step 2
1. GitHub answers with the `status` and `code` parameters which are parsed and send to our server to get the access token.
1. Our server responses with the access token.

![OAuth flow diagram](graphics/oauth-flow.png)  

## Considered Alternatives:
* *[ALTERNATIVE 1]* Perform the whole login process in the browser
* *[ALTERNATIVE 2]* Perform the whole login process in the backend
* *[ALTERNATIVE 3]* Mix both, frontend and backend to get the access token

## Conclusion
* *Chosen Alternative: [ALTERNATIVE 3]*
   because we can easily protect our client secret on the server, store the token safely in the clients local storage and 
   do not need to keep the state at the server. Further, it is possible to save additional user information in the local
   storage without the need for getting it every time from the server.

## Comparison
### [ALTERNATIVE 1]
* *+ good separation of concerns*
* *+ fast and direct communication between client and OAuth provider*
* *- the client secret must be stored in the javascript code* 

### [ALTERNATIVE 2]
* *+ the client secret can be stored safely*
* *- the server needs to maintain a state to remember the current state*

### [ALTERNATIVE 3]
* *+ pro arguments of both alternatives*
* *- more communication needed to perform the login*

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Lukas Harzenetter - initial API and implementation

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
