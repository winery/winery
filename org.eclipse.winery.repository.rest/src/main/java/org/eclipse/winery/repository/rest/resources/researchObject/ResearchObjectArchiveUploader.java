/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.researchObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.researchobject.ResearchObject;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.ResearchObjectUtils;
import org.eclipse.winery.repository.datatypes.ids.elements.ResearchObjectDirectoryId;
import org.eclipse.winery.repository.export.CsarExporter;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResearchObjectArchiveUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchObjectArchiveUploader.class);

    public static Response publishROAR(AbstractComponentInstanceResource resource, IRepository repository, String privacyOption) {

        String serverURL = Environments.getInstance().getUiConfig().getDarus().get("server");
        if (serverURL.endsWith("/")) {
            serverURL = serverURL.substring(0, serverURL.length() - 1);
        }
        String apiToken = Environments.getInstance().getUiConfig().getDarus().get("apiToken");
        String dataverse = Environments.getInstance().getUiConfig().getDarus().get("dataverse");
        ServiceTemplateId serviceTemplateId = (ServiceTemplateId) resource.getId();

        LOGGER.debug("Creating the dataset for {} ...", serviceTemplateId.getQName());
        LOGGER.debug("URL: {}, API-Token: {}, Dataverse: {}", serverURL, apiToken, dataverse);

        ResearchObjectDirectoryId researchObjectDirectoryId = new ResearchObjectDirectoryId(serviceTemplateId);
        ResearchObject ro = ResearchObjectUtils.getResearchObject(repository, researchObjectDirectoryId);

        String responseMessage;
        try {
            String jsonBody = getJsonBody(ro, privacyOption);
            String requestURL = serverURL + "/api/dataverses/" + dataverse + "/datasets";
            responseMessage = postCreateDataset(jsonBody, requestURL, apiToken);
        } catch (Exception e) {
            LOGGER.error("Error creating dataset", e);
            return Response.serverError().entity(getErrorMsg(e.getMessage())).build();
        }

        try {
            String datasetId = getDatasetId(responseMessage);
            String requestURL = serverURL + "/api/datasets/:persistentId/add?persistentId=" + datasetId;
            postUploadROAR(resource, repository, requestURL, apiToken);
            String datasetLocation = serverURL + "/dataset.xhtml?persistentId=" + datasetId;
            return Response.ok().header(HttpHeaders.LOCATION, datasetLocation).build();
        } catch (Exception e) {
            LOGGER.error("Error uploading roar", e);
            return Response.serverError().entity(getErrorMsg(e.getMessage())).build();
        }
    }

    private static String postCreateDataset(String body, String requestURL, String apiToken) throws Exception {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(requestURL);
            HttpEntity entity = new StringEntity(body, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("X-Dataverse-key", apiToken);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseMessage = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED) {
                LOGGER.debug("Dataset has been created successfully.");
                return responseMessage;
            }

            throw new Exception(responseMessage);
        }
    }

    private static void postUploadROAR(AbstractComponentInstanceResource resource, IRepository repository, String requestURL, String apiToken) throws Exception {

        CsarExporter exporter = new CsarExporter(repository);
        Map<String, Object> exportConfiguration = new HashMap<>();
        String filename = resource.getXmlId().getEncoded() + Constants.SUFFIX_CSAR;

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            exporter.writeRoarCsar(resource.getId(), os, exportConfiguration);

            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                 CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(requestURL);
                httpPost.setHeader("X-Dataverse-key", apiToken);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("file", is, ContentType.create("application/roar"), filename);
                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                String responseMessage = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                    LOGGER.debug("The roar {} has been uploaded successfully.", filename);
                    return;
                }

                throw new Exception(responseMessage);
            }
        }
    }

    private static String getDatasetId(String response) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response);

        return jsonResponse.get("data").findValue("persistentId").textValue();
    }

    private static String getErrorMsg(String response) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse;
        try {
            jsonResponse = mapper.readTree(response);
            return jsonResponse.get("message").textValue();
        } catch (Exception e) {
            return response;
        }
    }

    private static String getJsonBody(ResearchObject ro, String privacyOption) throws IOException {

        JsonNode template = getJsonTemplate();
        ((ObjectNode) template.at("/datasetVersion/metadataBlocks/privacy/fields/0")).put("value", privacyOption);
        JsonNode citationFields = template.at("/datasetVersion/metadataBlocks/citation/fields");
        Iterator<JsonNode> fields = citationFields.elements();

        while (fields.hasNext()) {
            JsonNode field = fields.next();
            switch (field.get("typeName").textValue()) {
                case "title":
                    ((ObjectNode) field).put("value", ro.getMetadata().getTitle());
                    break;
                case "author":
                    ((ObjectNode) field.at("/value/0/authorName")).put("value", ro.getMetadata().getAuthor());
                    break;
                case "datasetContact":
                    ((ObjectNode) field.at("/value/0/datasetContactEmail")).put("value", ro.getMetadata().getContact());
                    break;
                case "dsDescription":
                    ((ObjectNode) field.at("/value/0/dsDescriptionValue")).put("value", ro.getMetadata().getDescription());
                    break;
                case "subject":
                    ArrayNode arrayNode = ((ObjectNode) field).putArray("value");
                    for (String subject : ro.getMetadata().getSubjects().getSubject()) {
                        arrayNode.add(subject);
                    }
                    break;
                case "publication": {
                    ResearchObject.Publication pub = ro.getPublication();
                    if (pub != null) {
                        Iterator<JsonNode> publicationFields = field.at("/value/0").elements();
                        while (publicationFields.hasNext()) {
                            JsonNode publicationField = publicationFields.next();
                            switch (publicationField.get("typeName").textValue()) {
                                case "publicationCitation":
                                    updateNode(publicationFields, publicationField, pub.getCitation());
                                    break;
                                case "publicationIDType":
                                    updateNode(publicationFields, publicationField, pub.getIdType());
                                    break;
                                case "publicationIDNumber":
                                    updateNode(publicationFields, publicationField, pub.getId());
                                    break;
                                case "publicationURL":
                                    updateNode(publicationFields, publicationField, pub.getUrl());
                                    break;
                            }
                        }
                        if (field.at("/value/0").isEmpty()) {
                            fields.remove();
                        }
                    } else {
                        fields.remove();
                    }
                }
            }
        }
        return template.toString();
    }

    private static void updateNode(Iterator<JsonNode> publicationFields, JsonNode node, String value) {
        if (value != null && !value.isEmpty()) {
            ((ObjectNode) node).put("value", value);
        } else {
            publicationFields.remove();
        }
    }

    private static JsonNode getJsonTemplate() throws IOException {

        ClassLoader classLoader = ResearchObjectArchiveUploader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("darus-template.json");
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(inputStream, JsonNode.class);
    }
}
