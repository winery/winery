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

package org.eclipse.winery.repository.rest.resources.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.provenance.Provenance;
import org.eclipse.winery.provenance.ProvenanceFactory;
import org.eclipse.winery.provenance.exceptions.ProvenanceException;
import org.eclipse.winery.provenance.model.HistoryElement;
import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;
import org.eclipse.winery.provenance.model.authorization.AuthorizationNode;
import org.eclipse.winery.provenance.model.authorization.AuthorizationTree;
import org.eclipse.winery.repository.rest.resources.apiData.ProvenanceParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvenanceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationTree.class);

    private final Provenance provenance;
    private final String provenanceId;

    public ProvenanceResource(String provenanceId) throws ProvenanceException {
        provenance = ProvenanceFactory.getProvenance();

        this.provenanceId = Util.URLdecode(provenanceId);
        LOGGER.info("Provenance process identifier: " + provenanceId);
    }

    @GET
    @Path("history")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HistoryElement> getProvenance(@QueryParam("fileId") String fileId)
        throws ExecutionException, InterruptedException {
        ServiceTemplateId serviceTemplateId = new ServiceTemplateId(new QName(provenanceId));
        String qNameWithComponentVersionOnly = VersionUtils.getQNameWithComponentVersionOnly(serviceTemplateId);

        if (Objects.isNull(fileId) || "TOSCA-Metadata/TOSCA.meta".equals(fileId)) {
            return provenance
                .getHistory(qNameWithComponentVersionOnly)
                .get();
        } else {
            return provenance
                .getHistory(qNameWithComponentVersionOnly, fileId)
                .get();
        }
    }

    @GET
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AuthorizationNode> getAuthentication(@QueryParam("participantAddress") String participantAddress) throws ExecutionException, InterruptedException {
        AuthorizationInfo authorizationInfo = provenance.getAuthorization(provenanceId).get();

        if (Objects.nonNull(authorizationInfo)) {
            return authorizationInfo
                .getAuthorizationLineage(participantAddress)
                .orElseGet(ArrayList::new);
        }

        return new ArrayList<>();
    }

    @POST
    @Path("authorize")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addParticipant(ProvenanceParticipant participant) throws ExecutionException, InterruptedException {
        return provenance
            .authorize(Util.URLdecode(Util.URLdecode(provenanceId)), participant.authorizedEthereumAddress, participant.authorizedIdentity)
            .get();
    }
}
