/********************************************************************************
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

/**
 * Contains only the configuration entries that are managed by the backend.
 */
export class ConfigurationDTO {
    public blockchainNodeUrl: string;
    public activeKeystore: string;
    public keystorePassword: string;
    public authorizationSmartContractAddress: string;
    public provenanceSmartContractAddress: string;
    public swarmGatewayUrl: string;
}
