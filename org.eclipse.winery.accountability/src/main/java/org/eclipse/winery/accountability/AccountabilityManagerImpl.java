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

package org.eclipse.winery.accountability;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.model.FileProvenanceElement;
import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.ProvenanceVerification;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.common.HashingUtil;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes;
import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileParser;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationException;
import org.eclipse.virgo.util.parser.manifest.ManifestContents;
import org.eclipse.virgo.util.parser.manifest.RecoveringManifestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.accountability.model.ProvenanceVerification.ID_NOT_FOUND;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.INVALID;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.NO_AUTHORIZATION_DATA_AVAILABLE;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.NO_HASH_AVAILABLE;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.NO_MANIFEST_FILE_IN_PROVENANCE_LAYER;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.UNAUTHORIZED;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.VERIFIED;

public class AccountabilityManagerImpl implements AccountabilityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountabilityManagerImpl.class);
    private static final String NO_AUTHORIZATION_DATA = "No authorization data stored in the blockchain";
    private BlockchainAccess blockchain;
    private ImmutableStorageProvider storageProvider;

    AccountabilityManagerImpl(BlockchainAccess blockchainAccess, ImmutableStorageProvider storageProvider) {
        this.blockchain = blockchainAccess;
        this.storageProvider = storageProvider;
    }

    @Override
    public CompletableFuture<Map<String, ProvenanceVerification>> verify(String processIdentifier, String manifestId, Map<String, File> files) {
        Objects.requireNonNull(processIdentifier);
        Objects.requireNonNull(manifestId);
        Objects.requireNonNull(files);

        LOGGER.info("Verifying process id: " + processIdentifier);

        CompletableFuture<AuthorizationInfo> authorizationTree = this.blockchain.getAuthorizationTree(processIdentifier);

        return this.blockchain
            .getProvenance(processIdentifier)
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
                    LOGGER.error("Could not find the hashing algorithm.", e);
                } catch (IOException e) {
                    LOGGER.error("Could not transform file to byte array.", e);
                } catch (SerializationException e) {
                    LOGGER.error("Could not deserialize stored manifest file.", e);
                }

                return map;
            });
    }

    @Override
    public CompletableFuture<String> storeFingerprint(String processIdentifier, String fingerprint) {
        Objects.requireNonNull(processIdentifier);
        Objects.requireNonNull(fingerprint);

        return this.blockchain.saveFingerprint(processIdentifier, fingerprint);
    }

    @Override
    public CompletableFuture<Map<String, String>> storeState(Map<String, InputStream> files) {
        Objects.requireNonNull(files);
        Map<String, CompletableFuture<String>> allFuturesMap = new HashMap<>();

        for (Map.Entry<String, InputStream> entry : files.entrySet()) {
            allFuturesMap.put(entry.getKey(), storageProvider.store(entry.getValue()));
        }

        return CompletableFuture
            // execute all futures on parallel
            .allOf(allFuturesMap.values().toArray(new CompletableFuture[0]))
            // when all done, collect the results
            .thenApply((Void) -> {
                Map<String, String> result = new HashMap<>();

                for (Map.Entry<String, CompletableFuture<String>> entry : allFuturesMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().join());
                }

                return result;
            });
    }

    @Override
    public CompletableFuture<String> storeState(InputStream data) {
        Objects.requireNonNull(data);

        return storageProvider.store(data);
    }

    @Override
    public CompletableFuture<InputStream> retrieveState(String addressInImmutableStorage) {
        return storageProvider.retrieve(addressInImmutableStorage);
    }

    @Override
    public CompletableFuture<List<ModelProvenanceElement>> getHistory(String processIdentifier) {
        CompletableFuture<AuthorizationInfo> authorizationTree = this.blockchain.getAuthorizationTree(processIdentifier);

        return this.blockchain
            .getProvenance(processIdentifier)
            .thenCombine(authorizationTree, this::enhanceHistoryElements);
    }

    @Override
    public CompletableFuture<List<FileProvenanceElement>> getHistory(String processIdentifier, String fileId) {
        CompletableFuture<AuthorizationInfo> authorizationTree = this.blockchain.getAuthorizationTree(processIdentifier);

        return this.blockchain
            .getProvenance(processIdentifier)
            .thenCombine(authorizationTree, (provenanceElements, authorizationInfo) -> {
                List<FileProvenanceElement> result;

                if (authorizationInfo != null) {
                    result = getHistoryOfSingleFile(provenanceElements, fileId, authorizationInfo);
                } else {
                    LOGGER.info(NO_AUTHORIZATION_DATA);
                    FileProvenanceElement historyElement = new FileProvenanceElement("", 0, NO_AUTHORIZATION_DATA);
                    historyElement.setAuthorized(false);
                    historyElement.setFileName(fileId);
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

    @Override
    public void close() {
        this.blockchain.close();
        this.storageProvider.close();
    }

    private List<ModelProvenanceElement> enhanceHistoryElements(List<ModelProvenanceElement> historyElements, AuthorizationInfo authorizationInfo) {
        historyElements.forEach(element -> {
            element.setAuthorizedFlag(authorizationInfo);
            element.setAuthorName(
                authorizationInfo != null ?
                    authorizationInfo.getRealWorldIdentity(
                        element.getAuthorAddress()).orElseGet(String::new) : ""
            );
            this.fillFilesOfModel(element);
        });
        return historyElements;
    }

    protected List<FileProvenanceElement> getHistoryOfSingleFile(List<ModelProvenanceElement> historyElements, String fileId,
                                                                 AuthorizationInfo authorizationInfo) {
        List<FileProvenanceElement> history = new ArrayList<>();

        if (Objects.nonNull(historyElements) && historyElements.size() > 0) {
            for (ModelProvenanceElement modelProvenanceElement : historyElements) {
                if (Objects.nonNull(modelProvenanceElement.getFingerprint())) {
                    ManifestContents manifestContents = new RecoveringManifestParser()
                        .parse(modelProvenanceElement.getFingerprint());

                    for (String name : manifestContents.getSectionNames()) {
                        if (name.equals(fileId)) {
                            modelProvenanceElement.setAuthorizedFlag(authorizationInfo);
                            modelProvenanceElement.setAuthorName(
                                authorizationInfo.getRealWorldIdentity(
                                    modelProvenanceElement.getAuthorAddress()).orElseGet(String::new)
                            );

                            FileProvenanceElement currentFile = new FileProvenanceElement(modelProvenanceElement);
                            currentFile.setAddressInImmutableStorage(manifestContents.getAttributesForSection(name)
                                .get(TOSCAMetaFileAttributes.IMMUTABLE_ADDRESS));
                            currentFile.setFileHash(TOSCAMetaFileAttributes.HASH + "-" +
                                manifestContents.getAttributesForSection(name)
                                    .get(TOSCAMetaFileAttributes.HASH));
                            currentFile.setFileName(fileId);
                            history.add(currentFile);

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

    protected Map<String, ProvenanceVerification> validateBlockchainInput(List<ModelProvenanceElement> historyElements, String manifestId,
                                                                          Map<String, File> files, AuthorizationInfo authorizationInfo) throws IOException, NoSuchAlgorithmException {
        LOGGER.info("Start validating...");
        Map<String, ProvenanceVerification> verificationMap = new HashMap<>();
        ModelProvenanceElement validHistoryElement = verifyManifest(historyElements, manifestId, files, verificationMap, authorizationInfo);

        if (Objects.nonNull(validHistoryElement) && verificationMap.get(manifestId) == VERIFIED) {
            verifyFiles(validHistoryElement, files, verificationMap);
        }

        LOGGER.info("Completed validation.");

        return verificationMap;
    }

    protected ModelProvenanceElement verifyManifest(List<ModelProvenanceElement> historyElements, String manifestId, Map<String, File> files,
                                                    Map<String, ProvenanceVerification> verificationMap, AuthorizationInfo authorizationInfo) throws NoSuchAlgorithmException, IOException {
        LOGGER.info("Start validating manifest file...");
        ModelProvenanceElement validHistoryElement = null;
        ProvenanceVerification manifestVerification = INVALID;

        if (Objects.nonNull(historyElements) && historyElements.size() > 0) {
            File manifestFile = files.remove(manifestId);
            String checksum = HashingUtil.getChecksum(manifestFile, TOSCAMetaFileAttributes.HASH);
            for (ModelProvenanceElement element : historyElements) {
                String retrievedChecksum = HashingUtil.getChecksum(IOUtils.toInputStream(element.getFingerprint(), StandardCharsets.UTF_8), TOSCAMetaFileAttributes.HASH);
                if (retrievedChecksum.compareTo(checksum) == 0) {
                    validHistoryElement = element;
                    manifestVerification = authorizationInfo.isAuthorized(validHistoryElement.getAuthorAddress())
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

    protected void verifyFiles(ModelProvenanceElement validHistoryElement, Map<String, File> files, Map<String, ProvenanceVerification> verificationMap)
        throws SerializationException, IOException, NoSuchAlgorithmException {
        LOGGER.info("Start validating files...");
        for (Map.Entry<String, File> entry : files.entrySet()) {
            String fileId = entry.getKey();
            LOGGER.info("Validating \"" + fileId + "\"");
            String checksum = HashingUtil.getChecksum(entry.getValue(), TOSCAMetaFileAttributes.HASH);
            ProvenanceVerification verified = verifyFileInManifest(validHistoryElement, fileId, checksum);

            verificationMap.put(entry.getKey(), verified);
            LOGGER.info("\"" + entry.getKey() + "\" " + verified);
        }

        LOGGER.info("Completed files validation.");
    }

    protected ProvenanceVerification verifyFileInManifest(ModelProvenanceElement element, String fileId, String checksum) throws SerializationException {
        // 1. parse element.state as ManifestContent
        ManifestContents manifestContents = new RecoveringManifestParser()
            .parse(element.getFingerprint());

        if (manifestContents.getSectionNames().contains(fileId)) {
            String storedHash = manifestContents.getAttributesForSection(fileId).get(TOSCAMetaFileAttributes.HASH);
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

    private void fillFilesOfModel(ModelProvenanceElement model) {
        // 1. parse element.state as ManifestContent
        RecoveringManifestParser genericParser = new RecoveringManifestParser();
        ManifestContents manifestContents = genericParser.parse(model.getFingerprint());
        // 2. parse the ManifestContent as a TOSCAMetaFile
        TOSCAMetaFileParser parser = new TOSCAMetaFileParser();
        TOSCAMetaFile toscaMetaFile = parser.parse(manifestContents, genericParser.getProblems().size());
        //3. retrieve files from meta file
        Objects.requireNonNull(toscaMetaFile);

        List<FileProvenanceElement> result = toscaMetaFile
            .getFileBlocks()
            .stream()
            .map(
                fileSection -> {
                    FileProvenanceElement fileElement = new FileProvenanceElement(model);
                    fileElement.setFileHash(fileSection.get(TOSCAMetaFileAttributes.HASH));
                    fileElement.setAddressInImmutableStorage(fileSection.get(TOSCAMetaFileAttributes.IMMUTABLE_ADDRESS));
                    fileElement.setFileName(fileSection.get(TOSCAMetaFileAttributes.NAME));

                    return fileElement;
                }
            )
            .sorted(Comparator.comparing(FileProvenanceElement::getFileName))
            .collect(Collectors.toList());

        model.setFiles(result);
    }
}
