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

package org.eclipse.winery.provenance.blockchain;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.eclipse.winery.common.HashingUtil;
import org.eclipse.winery.provenance.Provenance;
import org.eclipse.winery.provenance.exceptions.BlockchainException;
import org.eclipse.winery.provenance.exceptions.ProvenanceException;
import org.eclipse.winery.provenance.model.HistoryElement;
import org.eclipse.winery.provenance.model.ProvenanceManifestFields;
import org.eclipse.winery.provenance.model.ProvenanceVerification;
import org.eclipse.winery.provenance.model.authorization.AuthorizationInfo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationException;
import org.eclipse.virgo.util.parser.manifest.ManifestContents;
import org.eclipse.virgo.util.parser.manifest.RecoveringManifestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.provenance.model.ProvenanceVerification.ID_NOT_FOUND;
import static org.eclipse.winery.provenance.model.ProvenanceVerification.INVALID;
import static org.eclipse.winery.provenance.model.ProvenanceVerification.NO_AUTHORIZATION_DATA_AVAILABLE;
import static org.eclipse.winery.provenance.model.ProvenanceVerification.NO_HASH_AVAILABLE;
import static org.eclipse.winery.provenance.model.ProvenanceVerification.NO_MANIFEST_FILE_IN_PROVENANCE_LAYER;
import static org.eclipse.winery.provenance.model.ProvenanceVerification.UNAUTHORIZED;
import static org.eclipse.winery.provenance.model.ProvenanceVerification.VERIFIED;

public class BlockchainProvenance implements Provenance {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainProvenance.class);

    private static final String NO_AUTHORIZATION_DATA = "No authorization data stored in the blockchain";

    private BlockchainAccess blockchain;

    public BlockchainProvenance() throws ProvenanceException {
        this(BlockchainFactory.AvailableBlockchains.ETHEREUM);
    }

    BlockchainProvenance(BlockchainFactory.AvailableBlockchains desiredBlockchain) throws ProvenanceException {
        try {
            this.blockchain = BlockchainFactory.getBlockchainAccess(desiredBlockchain);
        } catch (BlockchainException e) {
            String msg = "Could not instantiate provenance layer";
            LOGGER.error(msg, e);
            throw new ProvenanceException(msg, e);
        }
    }

    @Override
    public CompletableFuture<Map<String, ProvenanceVerification>> verify(String id, String manifestId, Map<String, File> files) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(manifestId);
        Objects.requireNonNull(files);

        LOGGER.info("Verifying process id: " + id);

        CompletableFuture<AuthorizationInfo> authorizationTree = this.blockchain.getAuthorizationTree(id);

        return this.blockchain
            .getProvenance(id)
            .thenCombine(authorizationTree, (provenanceElements, authorizationInfo) -> {
                // TODO: extract into one method and write test cases
                Map<String, ProvenanceVerification> map = null;
                try {
                    if (authorizationInfo == null) {
                        LOGGER.info(NO_AUTHORIZATION_DATA);
                        map = new HashMap<>();
                        map.put(manifestId, NO_AUTHORIZATION_DATA_AVAILABLE);
                    } else {
                        map = this.validateBlockchainInput(provenanceElements, manifestId, files, authorizationInfo);
                    }
                } catch (NoSuchAlgorithmException e) {
                    LOGGER.error("Could not find the hash algorithm.", e);
                } catch (IOException e) {
                    LOGGER.error("Could not transform file to byte array.", e);
                } catch (SerializationException e) {
                    LOGGER.error("Could not deserialize stored manifest file.", e);
                }

                return map;
            });
    }

    @Override
    public CompletableFuture<String> storeState(String id, String state) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(state);

        return this.blockchain.saveState(id, state);
    }

    @Override
    public CompletableFuture<List<HistoryElement>> getHistory(String id) {
        CompletableFuture<AuthorizationInfo> authorizationTree = this.blockchain.getAuthorizationTree(id);

        return this.blockchain
            .getProvenance(id)
            .thenCombine(authorizationTree, this::enhanceHistoryElements);
    }

    @Override
    public CompletableFuture<List<HistoryElement>> getHistory(String id, String fileId) {
        CompletableFuture<AuthorizationInfo> authorizationTree = this.blockchain.getAuthorizationTree(id);

        return this.blockchain
            .getProvenance(id)
            .thenCombine(authorizationTree, (provenanceElements, authorizationInfo) -> {
                List<HistoryElement> result = null;

                if (authorizationInfo != null) {
                    result = getHistoryOfSingleFile(provenanceElements, fileId, authorizationInfo);
                } else {
                    LOGGER.info(NO_AUTHORIZATION_DATA);
                    HistoryElement historyElement = new HistoryElement("", 0, 0, NO_AUTHORIZATION_DATA, "");
                    historyElement.setAuthorized(false);
                    result = Collections.singletonList(historyElement);
                }

                return result;
            });
    }

    @Override
    public CompletableFuture<String> authorize(String processIdentifier, String authorizedEthereumAddress, String authorizedIdentity) {
        LOGGER.info("Authorizing \"" + authorizedEthereumAddress + "\" for " + processIdentifier);
        return this.blockchain.authorize(processIdentifier, authorizedEthereumAddress, authorizedIdentity);
    }

    @Override
    public CompletableFuture<AuthorizationInfo> getAuthorization(String processIdentifier) {
        LOGGER.info("Retrieving authorization info for process " + processIdentifier);
        return this.blockchain.getAuthorizationTree(processIdentifier);
    }

    List<HistoryElement> enhanceHistoryElements(List<HistoryElement> historyElements, AuthorizationInfo authorizationInfo) {
        historyElements.forEach(element -> {
            element.setAuthorizedFlag(authorizationInfo);
            element.setName(
                authorizationInfo.getRealWorldIdentity(
                    element.getStateSetterAddress()).orElseGet(String::new)
            );
        });
        return historyElements;
    }

    List<HistoryElement> getHistoryOfSingleFile(List<HistoryElement> historyElements, String fileId,
                                                AuthorizationInfo authorizationInfo) {
        List<HistoryElement> history = new ArrayList<>();

        if (Objects.nonNull(historyElements) && historyElements.size() > 0) {
            for (HistoryElement element : historyElements) {
                if (Objects.nonNull(element.getState())) {
                    ManifestContents manifestContents = new RecoveringManifestParser()
                        .parse(element.getState());

                    for (String name : manifestContents.getSectionNames()) {
                        if (name.equals(fileId)) {
                            history.add(element);
                            element.setAuthorizedFlag(authorizationInfo);
                            element.setName(
                                authorizationInfo.getRealWorldIdentity(
                                    element.getStateSetterAddress()).orElseGet(String::new)
                            );
                            element.setFileHash(
                                ProvenanceManifestFields.HASH + "-" +
                                    manifestContents.getAttributesForSection(name)
                                        .get(ProvenanceManifestFields.HASH)
                            );
                            break;
                        }
                    }
                }
            }
        }

        if (history.size() > 0)
            return history;

        return null;
    }

    Map<String, ProvenanceVerification> validateBlockchainInput(List<HistoryElement> historyElements, String manifestId,
                                                                Map<String, File> files, AuthorizationInfo authorizationInfo) throws IOException, NoSuchAlgorithmException {
        LOGGER.info("Start validating...");
        Map<String, ProvenanceVerification> verificationMap = new HashMap<>();
        HistoryElement validHistoryElement = verifyManifest(historyElements, manifestId, files, verificationMap, authorizationInfo);

        if (Objects.nonNull(validHistoryElement) && verificationMap.get(manifestId) == VERIFIED) {
            verifyFiles(validHistoryElement, files, verificationMap);
        }

        LOGGER.info("Completed validation.");

        return verificationMap;
    }

    HistoryElement verifyManifest(List<HistoryElement> historyElements, String manifestId, Map<String, File> files,
                                  Map<String, ProvenanceVerification> verificationMap, AuthorizationInfo authorizationInfo) throws NoSuchAlgorithmException, IOException {
        LOGGER.info("Start validating manifest file...");
        HistoryElement validHistoryElement = null;
        ProvenanceVerification manifestVerification = INVALID;

        if (Objects.nonNull(historyElements) && historyElements.size() > 0) {
            File manifestFile = files.remove(manifestId);
            String checksum = HashingUtil.getChecksum(IOUtils.toByteArray(manifestFile.toURI()), ProvenanceManifestFields.HASH);
            for (HistoryElement element : historyElements) {
                String retrievedChecksum = HashingUtil.getChecksum(element.getState().getBytes(), ProvenanceManifestFields.HASH);
                if (retrievedChecksum.compareTo(checksum) == 0) {
                    validHistoryElement = element;
                    manifestVerification = authorizationInfo.isAuthorized(validHistoryElement.getStateSetterAddress())
                        ? VERIFIED
                        : UNAUTHORIZED;
                    break;
                }
            }
        } else {
            manifestVerification = NO_MANIFEST_FILE_IN_PROVENANCE_LAYER;
        }

        LOGGER.info("Manifest verification resulted in " + manifestVerification);
        verificationMap.put(manifestId, manifestVerification);

        return validHistoryElement;
    }

    void verifyFiles(HistoryElement validHistoryElement, Map<String, File> files, Map<String, ProvenanceVerification> verificationMap)
        throws SerializationException, IOException, NoSuchAlgorithmException {
        LOGGER.info("Start validating files...");
        for (Map.Entry<String, File> entry : files.entrySet()) {
            String fileId = entry.getKey();
            LOGGER.info("Validating \"" + fileId + "\"");

            byte[] serialize = IOUtils.toByteArray(entry.getValue().toURI());
            String checksum = HashingUtil.getChecksum(serialize, ProvenanceManifestFields.HASH);
            ProvenanceVerification verified = verifyFileInManifest(validHistoryElement, fileId, checksum);

            verificationMap.put(entry.getKey(), verified);
            LOGGER.info("\"" + entry.getKey() + "\" " + verified);
        }

        LOGGER.info("Completed files validation.");
    }

    ProvenanceVerification verifyFileInManifest(HistoryElement element, String fileId, String checksum) throws SerializationException {
        // 1. parse element.state as ManifestContent
        ManifestContents manifestContents = new RecoveringManifestParser()
            .parse(element.getState());

        if (manifestContents.getSectionNames().contains(fileId)) {
            String storedHash = manifestContents.getAttributesForSection(fileId).get(ProvenanceManifestFields.HASH);
            if (Objects.isNull(storedHash)) {
                return NO_HASH_AVAILABLE;
            } else if (storedHash.equals(checksum)) {
                // 2.1 if section == fileId and hash == checksum -> file verified
                return VERIFIED;
            } else {
                return INVALID;
            }
        }

        return ID_NOT_FOUND;
    }
}
