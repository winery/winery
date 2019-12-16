/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
pragma solidity ^0.4.21;

contract authorization {
    // This event logs one authorization record for a given collaboration process. A check needs to be made (at the app
    // level) that the record is connected with the ServiceOwner with a path. The ServiceOwner is the first authorizer of
    // a given collaboration process.
    event Authorized(string indexed _resourceIdentifier, address indexed _authorizer, address indexed _authorized, string realWorldIdentity);

    function authorize(string _resourceIdentifier, address _authorized, string _realWorldIdentity) public {
        emit Authorized(_resourceIdentifier, msg.sender, _authorized, _realWorldIdentity);
    }
}
