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
package org.eclipse.winery.repository.rest.resources.admin;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.configuration.AccountabilityConfigurationManager;
import org.eclipse.winery.common.configuration.AccountabilityConfigurationObject;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.repository.rest.resources.apiData.AccountabilityConfigurationData;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class AccountabilityConfigurationResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountabilityConfiguration() {
        AccountabilityConfigurationObject props = Environments.getInstance().getAccountabilityConfig();
        AccountabilityConfigurationData result = new AccountabilityConfigurationData();
        result.setAuthorizationSmartContractAddress(props.getEthereumAuthorizationSmartContractAddress());
        result.setProvenanceSmartContractAddress(props.getEthereumProvenanceSmartContractAddress());
        result.setBlockchainNodeUrl(props.getGethUrl());
        result.setActiveKeystore(props.getEthereumCredentialsFileName());
        result.setKeystorePassword(props.getEthereumPassword());
        result.setSwarmGatewayUrl(props.getSwarmGatewayUrl());
        return Response.ok(result).build();
    }

    @PUT
    @Consumes( {MediaType.MULTIPART_FORM_DATA})
    public Response setAccountabilityConfiguration(
        @FormDataParam("keystoreFile") InputStream keystoreFileStream,
        @FormDataParam("keystoreFile") FormDataContentDisposition disposition,
        @FormDataParam("blockhainNodeUrl") String blockchainNodeUrl,
        @FormDataParam("keystorePassword") String keystorePassword,
        @FormDataParam("authorizationSmartContractAddress") String authorizationSmartContractAddress,
        @FormDataParam("provenanceSmartContractAddress") String provenanceSmartContractAddress,
        @FormDataParam("swarmGatewayUrl") String swarmGatewayUrl
    ) {
        AccountabilityConfigurationManager manager = AccountabilityConfigurationManager.getInstance();
        try {
            // sending a new keystore file is optional
            if (keystoreFileStream != null && disposition != null) {
                manager.setNewKeystoreFile(keystoreFileStream, disposition.getFileName());
            }
            AccountabilityConfigurationObject props = Environments.getInstance().getAccountabilityConfig();
            props.setEthereumAuthorizationSmartContractAddress(authorizationSmartContractAddress);
            props.setEthereumProvenanceSmartContractAddress(provenanceSmartContractAddress);
            props.setGethUrl(blockchainNodeUrl);
            props.setEthereumPassword(keystorePassword);
            props.setSwarmGatewayUrl(swarmGatewayUrl);
            Environments.save(props);
            return Response.noContent().build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    public Response restoreDefaults() throws IOException {
        //Refactor this when the manager is moved to accountability
        AccountabilityConfigurationManager manager = AccountabilityConfigurationManager.getInstance();
        manager.restoreDefaults();
        return Response.noContent().build();
    }
}
