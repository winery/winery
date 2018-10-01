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

contract Provenance {
    // this event logs one state version of the collaboration resource in order to buildProvenanceSmartContract a complete provenance of it
    event ResourceVersion(string indexed _resourceIdentifier, address indexed _creator, bytes _compressedResource);
    
    function addResourceVersion(string _resourceIdentifier, bytes _compressedResource) public {
        emit ResourceVersion(_resourceIdentifier, msg.sender, _compressedResource);
    }
}
