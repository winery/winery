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
import { ConfigurationDTO } from './ConfigurationDTO';

export class Configuration extends ConfigurationDTO {
    // This entry is persisted in browser's local storage
    private _enableAccountability: boolean;

    constructor(dto: ConfigurationDTO) {
        super();
        this.blockchainNodeUrl = dto.blockchainNodeUrl;
        this.activeKeystore = dto.activeKeystore;
        this.keystorePassword = dto.keystorePassword;
        this.authorizationSmartContractAddress = dto.authorizationSmartContractAddress;
        this.provenanceSmartContractAddress = dto.provenanceSmartContractAddress;
        this.swarmGatewayUrl = dto.swarmGatewayUrl;
    }

    generateDTO(): ConfigurationDTO {
        const dto: ConfigurationDTO = new ConfigurationDTO();
        dto.blockchainNodeUrl = this.blockchainNodeUrl;
        dto.activeKeystore = this.activeKeystore;
        dto.keystorePassword = this.keystorePassword;
        dto.authorizationSmartContractAddress = this.authorizationSmartContractAddress;
        dto.provenanceSmartContractAddress = this.provenanceSmartContractAddress;
        dto.swarmGatewayUrl = this.swarmGatewayUrl;

        return dto;
    }

    get enableAccountability(): boolean {
        return this._enableAccountability;
    }

    set enableAccountability(value: boolean) {
        this._enableAccountability = value;
    }
}
