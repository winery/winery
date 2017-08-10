/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */

export const sections = {
    nodetypes : 'nodeType',
    servicetemplates : 'serviceTemplate',
    relationshiptypes : 'relationshipType',
    artifacttypes: 'artifactType',
    artifacttemplates: 'artifactTemplate',
    requirementtypes: 'requirementType',
    capabilitytypes: 'capabilityType',
    nodetypeimplementations : 'nodeTypeImplementation',
    relationshiptypeimplementations : 'relationshipTypeImplementation',
    policytypes: 'policyType',
    policytemplates: 'policyTemplate',
    imports: 'xSDImport',
};

// when running in development mode on port 3000, use default port 8080
// otherwise, assume that backend runs on the some port
export const hostURL = location.protocol + '//' + location.hostname + ':' + (location.port === '3000' ? '8080' : location.port);
export const backendBaseURL = hostURL + '/winery';
