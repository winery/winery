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
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.backend.AccountabilityConfigurationManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.apiData.AccountabilityConfigurationData;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

public class AccountabilityConfigurationResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountabilityConfiguration() {
        Properties props = RepositoryFactory.getRepository().getAccountabilityConfigurationManager().properties;
        AccountabilityConfigurationData result = new AccountabilityConfigurationData();
        result.setAuthorizationSmartContractAddress(props.getProperty("ethereum-authorization-smart-contract-address"));
        result.setProvenanceSmartContractAddress(props.getProperty("ethereum-provenance-smart-contract-address"));
        result.setBlockchainNodeUrl(props.getProperty("geth-url"));
        result.setActiveKeystore(props.getProperty("ethereum-credentials-file-name"));
        result.setKeystorePassword(props.getProperty("ethereum-password"));
        result.setSwarmGatewayUrl(props.getProperty("swarm-gateway-url"));

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
        AccountabilityConfigurationManager manager = RepositoryFactory.getRepository().getAccountabilityConfigurationManager();
        try {

            // sending a new keystore file is optional
            if (keystoreFileStream != null && disposition != null) {
                manager.setNewKeystoreFile(keystoreFileStream, disposition.getFileName());
            }
            Properties props = manager.properties;
            props.setProperty("ethereum-authorization-smart-contract-address", authorizationSmartContractAddress);
            props.setProperty("ethereum-provenance-smart-contract-address", provenanceSmartContractAddress);
            props.setProperty("geth-url", blockchainNodeUrl);
            props.setProperty("ethereum-password", keystorePassword);
            props.setProperty("swarm-gateway-url", swarmGatewayUrl);

            manager.saveProperties();
            return Response.noContent().build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    public Response restoreDefaults() {
        AccountabilityConfigurationManager manager = RepositoryFactory.getRepository().getAccountabilityConfigurationManager();
        try {
            manager.restoreDefaults();

            return Response.noContent().build();
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
