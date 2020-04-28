/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.eclipse.winery.accountability.blockchain.BlockchainAccess;
import org.eclipse.winery.accountability.blockchain.BlockchainFactory;
import org.eclipse.winery.accountability.model.FileProvenanceElement;
import org.eclipse.winery.accountability.model.ModelProvenanceElement;
import org.eclipse.winery.accountability.model.ProvenanceVerification;
import org.eclipse.winery.accountability.model.authorization.AuthorizationElement;
import org.eclipse.winery.accountability.model.authorization.AuthorizationInfo;
import org.eclipse.winery.accountability.model.authorization.AuthorizationTree;
import org.eclipse.winery.accountability.storage.ImmutableStorageProvider;
import org.eclipse.winery.accountability.storage.ImmutableStorageProviderFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.eclipse.winery.accountability.model.ProvenanceVerification.ID_NOT_FOUND;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.INVALID;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.NO_HASH_AVAILABLE;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.NO_MANIFEST_FILE_IN_PROVENANCE_LAYER;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.UNAUTHORIZED;
import static org.eclipse.winery.accountability.model.ProvenanceVerification.VERIFIED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@Disabled("This test seems to fail transiently on test infrastructure")
class AccountabilityManagerImplTest {

    private AccountabilityManagerImpl provenance;

    @BeforeEach
    public void setUp() throws Exception {
        BlockchainAccess blockchainAccess = BlockchainFactory
            .getBlockchainAccess(BlockchainFactory.AvailableBlockchains.TEST, null);
        ImmutableStorageProvider storageProvider = ImmutableStorageProviderFactory
            .getStorageProvider(ImmutableStorageProviderFactory.AvailableImmutableStorages.TEST, null);
        this.provenance = new AccountabilityManagerImpl(blockchainAccess, storageProvider);
    }

    @ParameterizedTest(name = "{index} => ''{6}''")
    @MethodSource("verifyFileInManifestArguments")
    public void verifyFileInManifest(String fileId, String checksum, String manifestString, String expectedFileId,
                                     String expectedChecksum, ProvenanceVerification expectedVerification, String description) {
        ModelProvenanceElement historyElement = new ModelProvenanceElement();
        historyElement.setFingerprint(String.format(manifestString, fileId, checksum));

        assertEquals(expectedVerification, provenance.verifyFileInManifest(historyElement, expectedFileId, expectedChecksum));
    }

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("verifyManifestArguments")
    public void verifyManifest(ModelProvenanceElement historyElement, ProvenanceVerification expectedVerification, AuthorizationInfo authorizationInfo,
                               String description) throws Exception {
        String manifestId = "Tosca.meta";
        List<ModelProvenanceElement> historyElementList = Objects.nonNull(historyElement) ? Arrays.asList(historyElement) : null;

        Map<String, File> filesMap = new HashMap<>();
        filesMap.put(manifestId, new File(ClassLoader.getSystemResource(manifestId).toURI()));

        Map<String, ProvenanceVerification> verificationMap = new HashMap<>();

        provenance.verifyManifest(historyElementList, manifestId, filesMap, verificationMap, authorizationInfo);
        ProvenanceVerification verification = verificationMap.get(manifestId);

        assertEquals(expectedVerification, verification);
    }

    @ParameterizedTest(name = "{index} => ''{5}''")
    @MethodSource("validateBlockchainInputArguments")
    public void validateBlockchainInput(ProvenanceVerification expectedVerification, List<ModelProvenanceElement> historyElements,
                                        Map<String, File> filesHashMap, AuthorizationInfo authorizationInfo, String description) {

        Exception ee = null;
        try {
            Map<String, ProvenanceVerification> entries = provenance.validateBlockchainInput(historyElements, "Tosca.meta", filesHashMap, authorizationInfo);
            assertNotNull(entries);
            assumingThat(
                Objects.nonNull(expectedVerification),
                () -> {
                    for (Map.Entry<String, ProvenanceVerification> entry : entries.entrySet()) {
                        assertEquals(expectedVerification, entry.getValue());
                    }
                }
            );
        } catch (IOException | NoSuchAlgorithmException e) {
            ee = e;
        }

        Assertions.assertNull(ee);
    }

    @ParameterizedTest(name = "{index} => ''{3}''")
    @MethodSource("verifyFileInProvenanceElementArguments")
    public void verifyFilesInProvenanceElement(Map<String, ProvenanceVerification> expectedVerification, ModelProvenanceElement historyElements,
                                               Map<String, File> filesHashMap, String description) throws Exception {
        Map<String, ProvenanceVerification> verificationMap = new HashMap<>();

        provenance.verifyFiles(historyElements, filesHashMap, verificationMap);

        expectedVerification.forEach((key, value) -> {
            assertEquals(value, verificationMap.get(key));
            assertNotNull(verificationMap.remove(key));
        });
        assertEquals(0, verificationMap.size());
    }

    @ParameterizedTest(name = "{index} => ''{5}''")
    @MethodSource("getHistoryOfSingleFileArguments")
    public void getHistoryOfSingleFile(List<ModelProvenanceElement> historyElements, String fileId, List<FileProvenanceElement> expectedElements,
                                       AuthorizationInfo authorizationInfo, boolean[] isAuthorized, String description) {
        List<FileProvenanceElement> actual = provenance.getHistoryOfSingleFile(historyElements, fileId, authorizationInfo);

        assumingThat(Objects.nonNull(expectedElements),
            () -> {
                assertNotNull(actual);
                assertEquals(expectedElements.size(), actual.size());
                for (FileProvenanceElement historyElement : actual) {
                    assertTrue(expectedElements.indexOf(historyElement) >= 0);
                }
            });
        assumingThat(
            Objects.nonNull(isAuthorized),
            () -> {
                for (FileProvenanceElement historyElement : actual) {
                    assertEquals(isAuthorized[actual.indexOf(historyElement)], historyElement.isAuthorized());
                }
            }
        );
    }

    @ParameterizedTest(name = "{index} => ''{2}''")
    @MethodSource("fileUploadToImmutableStorageArguments")
    public void fileUploadToImmutableStorage(Map<String, InputStream> files, String description) {
        try {
            provenance.storeState(files)
                .whenComplete((result, error) -> {
                    assertNull(error);
                    assertNotNull(result);
                    assertEquals(files.size(), result.size());
                    files.forEach((key, value) -> {
                        assertTrue(result.containsKey(key));
                    });
                }).get();
        } catch (InterruptedException | ExecutionException ignored) {

        }
    }

    private static Stream<Arguments> verifyFileInManifestArguments() {
        return Stream.of(
            Arguments.of(
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                "TOSCA-Meta-Version: 1.0\n" +
                    "CSAR-Version: 1.0\n" +
                    "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                    "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                    "\n" +
                    "Name: %s\n" +
                    "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                    "SHA-256: %s\n",
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                VERIFIED,
                "Simple valid file"
            ),
            Arguments.of(
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                "TOSCA-Meta-Version: 1.0\n" +
                    "CSAR-Version: 1.0\n" +
                    "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                    "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                    "\n" +
                    "Name: %s\n" +
                    "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                    "SHA-256: %s\n",
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768cff1b7a68b95faed372544",
                INVALID,
                "Simple invalid file"
            ),
            Arguments.of(
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                "TOSCA-Meta-Version: 1.0\n" +
                    "CSAR-Version: 1.0\n" +
                    "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                    "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                    "\n" +
                    "Name: %s\n" +
                    "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                    "\n" +
                    "Name: %s\n" +
                    "Content-Type: application/vnd.oasis.tosca.definitions\n",
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                NO_HASH_AVAILABLE,
                "Simple no hash available file"
            ),
            Arguments.of(
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                "TOSCA-Meta-Version: 1.0\n" +
                    "CSAR-Version: 1.0\n" +
                    "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                    "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                    "\n" +
                    "Name: %sAD\n" +
                    "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                    "SHA-256: %s\n",
                "Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca",
                "9e69d43768c487b7a68b95faed372544",
                ID_NOT_FOUND,
                "Id of file not found"
            )
        );
    }

    private static Stream<Arguments> verifyManifestArguments() {
        String validState = "TOSCA-Meta-Version: 1.0\n" +
            "CSAR-Version: 1.0\n" +
            "Created-By: Winery 2.0.0-SNAPSHOT\n" +
            "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
            "\n" +
            "Name: myTestFile.tosca\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: 97193968948686d6947d4d760d3fe724b9981980056b8902be92e91fbe9e3eed\n" +
            "\n" +
            "Name: myTestFile2.tosca\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: a99c9a8e954355e34f11282a6dec25cc5e0b3aec2fd25d33daf2ca06267b477e\n";

        ModelProvenanceElement validElement = new ModelProvenanceElement();
        validElement.setFingerprint(validState);
        validElement.setAuthorAddress("0x11111");

        ModelProvenanceElement invalidElement = new ModelProvenanceElement();
        invalidElement.setFingerprint("Empty");
        invalidElement.setAuthorAddress("0x11111");

        ModelProvenanceElement unauthorizedElement = new ModelProvenanceElement();
        unauthorizedElement.setFingerprint("TOSCA-Meta-Version: 1.0\n" +
            "CSAR-Version: 1.0\n" +
            "Created-By: Winery 2.0.0-SNAPSHOT\n" +
            "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
            "\n" +
            "Name: myTestFile.tosca\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: 97193968948686d6947d4d760d3fe724b9981980056b8902be92e91fbe9e3eed\n" +
            "\n" +
            "Name: myTestFile2.tosca\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: a99c9a8e954355e34f11282a6dec25cc5e0b3aec2fd25d33daf2ca06267b477e\n");
        unauthorizedElement.setAuthorAddress("0x11234111");

        AuthorizationElement authorizationElement = new AuthorizationElement();
        authorizationElement.setAuthorizerBlockchainAddress("0x11111");
        authorizationElement.setAuthorizedBlockchainAddress("0x11111");
        authorizationElement.setAuthorizedIdentity("Gharreb");
        authorizationElement.setTransactionHash("0x3215F23");
        List<AuthorizationElement> authList = Arrays.asList(authorizationElement);
        AuthorizationInfo authorizationInfo = new AuthorizationTree(authList);

        return Stream.of(
            Arguments.of(
                validElement,
                VERIFIED,
                authorizationInfo,
                "Expect a verified manifest"
            ),
            Arguments.of(
                invalidElement,
                INVALID,
                authorizationInfo,
                "Expect an invalid manifest"
            ),
            Arguments.of(
                null,
                NO_MANIFEST_FILE_IN_PROVENANCE_LAYER,
                authorizationInfo,
                "Expect no manifest in provenance"
            ),
            Arguments.of(
                unauthorizedElement,
                UNAUTHORIZED,
                authorizationInfo,
                "Expect a unauthorized manifest"
            )
        );
    }

    private static Stream<Arguments> validateBlockchainInputArguments() throws Exception {
        String manifestFile = "TOSCA-Meta-Version: 1.0\n" +
            "CSAR-Version: 1.0\n" +
            "Created-By: Winery 2.0.0-SNAPSHOT\n" +
            "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
            "\n" +
            "Name: myTestFile.tosca\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: 97193968948686d6947d4d760d3fe724b9981980056b8902be92e91fbe9e3eed\n" +
            "\n" +
            "Name: myTestFile2.tosca\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: a99c9a8e954355e34f11282a6dec25cc5e0b3aec2fd25d33daf2ca06267b477e\n";

        ModelProvenanceElement element = new ModelProvenanceElement();
        element.setFingerprint(manifestFile);
        element.setAuthorAddress("0x11111");

        Map<String, File> validFilesHashMap = new HashMap<>();
        validFilesHashMap.put("myTestFile.tosca", new File(ClassLoader.getSystemClassLoader().getResource("emptyDefinition.tosca").toURI()));
        validFilesHashMap.put("myTestFile2.tosca", new File(ClassLoader.getSystemClassLoader().getResource("secondDefinition.tosca").toURI()));
        validFilesHashMap.put("Tosca.meta", new File(ClassLoader.getSystemClassLoader().getResource("Tosca.meta").toURI()));

        AuthorizationElement authorizationElement = new AuthorizationElement();
        authorizationElement.setAuthorizerBlockchainAddress("0x11111");
        authorizationElement.setAuthorizedBlockchainAddress("0x11111");
        authorizationElement.setAuthorizedIdentity("Gharreb");
        authorizationElement.setTransactionHash("0x3215F23");
        List<AuthorizationElement> authList = Arrays.asList(authorizationElement);
        AuthorizationInfo authorizationInfo = new AuthorizationTree(authList);

        return Stream.of(
            Arguments.of(
                VERIFIED,
                Collections.singletonList(element),
                validFilesHashMap,
                authorizationInfo,
                "Simple valid file in manifest"
            )
        );
    }

    private static Stream<Arguments> getHistoryOfSingleFileArguments() {
        String fileId = "folder/myFile.tosca";

        ModelProvenanceElement elementWithFileOccurrence_0 = new ModelProvenanceElement("0x1000000000000000000000000000000000000000", 0, "0x11111",
            // region string
            "TOSCA-Meta-Version: 1.0\n" +
                "CSAR-Version: 1.0\n" +
                "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                "\n" +
                "Name: Definitions/myTestFile.tosca\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b7a68b95faed372544\n" +
                "\n" +
                "Name: " + fileId + "\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b7a68b95faed372544"
            // endregion
        );
        ModelProvenanceElement elementWithFileOccurrence_1 = new ModelProvenanceElement("0x2000000000000000000000000000000000000000", 0, "0x3333",
            // region string
            "TOSCA-Meta-Version: 1.0\n" +
                "CSAR-Version: 1.0\n" +
                "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                "\n" +
                "Name: Definitions/myTestFile.tosca\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b7a68b95faed372544\n" +
                "\n" +
                "Name: " + fileId + "\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b9e69d43768c487b7a68b95faed3725444" +
                "\n" +
                "Name: Definitions/myTestFile2.tosca\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b7a68b95faed372544"
            // endregion
        );
        ModelProvenanceElement elementWithoutFileOccurrence_0 = new ModelProvenanceElement("0x3000000000000000000000000000000000000000", 0, "0x11111",
            // region string
            "TOSCA-Meta-Version: 1.0\n" +
                "CSAR-Version: 1.0\n" +
                "Created-By: Winery 2.0.0-SNAPSHOT\n" +
                "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
                "\n" +
                "Name: Definitions/myTestFile.tosca\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b7a68b95faed372544\n" +
                "\n" +
                "Name: Definitions/myTestFile1.tosca\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b9e69d43768c487b7a68b95faed3725444" +
                "\n" +
                "Name: Definitions/myTestFile2.tosca\n" +
                "Content-Type: application/vnd.oasis.tosca.definitions\n" +
                "SHA-256: 9e69d43768c487b7a68b95faed372544"
            // endregion
        );

        AuthorizationElement authorizationElement = new AuthorizationElement();
        authorizationElement.setAuthorizerBlockchainAddress("0x11111");
        authorizationElement.setAuthorizedBlockchainAddress("0x11111");
        authorizationElement.setAuthorizedIdentity("Gharreb");
        authorizationElement.setTransactionHash("0x3215F230003215F230003215F230003215F23000");
        List<AuthorizationElement> authList = Arrays.asList(authorizationElement);
        AuthorizationInfo authorizationInfo = new AuthorizationTree(authList);

        return Stream.of(
            Arguments.of(
                null,
                "someId",
                null,
                authorizationInfo,
                null,
                "Empty provenance elements list"
            ),
            Arguments.of(
                Arrays.asList(elementWithFileOccurrence_0, elementWithoutFileOccurrence_0, elementWithFileOccurrence_1),
                fileId,
                Arrays.asList(new FileProvenanceElement(elementWithFileOccurrence_0), new FileProvenanceElement(elementWithFileOccurrence_1)),
                authorizationInfo,
                new boolean[] {true, false},
                "Find two file occurrences"
            )
        );
    }

    private static Stream<Arguments> verifyFileInProvenanceElementArguments() throws URISyntaxException {
        String file0 = "myTestFile.tosca";
        String file1 = "myTestFile2.tosca";
        String missingFileManifest = "TOSCA-Meta-Version: 1.0\n" +
            "CSAR-Version: 1.0\n" +
            "Created-By: Winery 2.0.0-SNAPSHOT\n" +
            "Entry-Definitions: Definitions/servicetemplates1__MyTinyToDo_Bare_Docker.tosca\n" +
            "\n" +
            "Name: " + file0 + "\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n" +
            "SHA-256: 97193968948686d6947d4d760d3fe724b9981980056b8902be92e91fbe9e3eed\n";
        String missingHashManifest = missingFileManifest +
            "\n" +
            "Name: " + file1 + "\n" +
            "Content-Type: application/vnd.oasis.tosca.definitions\n";
        String completeManifestFile = missingHashManifest +
            "SHA-256: a99c9a8e954355e34f11282a6dec25cc5e0b3aec2fd25d33daf2ca06267b477e";

        ModelProvenanceElement element = new ModelProvenanceElement();
        element.setFingerprint(completeManifestFile);
        ModelProvenanceElement invalidElement = new ModelProvenanceElement();
        invalidElement.setFingerprint(completeManifestFile.substring(0, completeManifestFile.length() - 5) + "1111");
        ModelProvenanceElement elementWithNoHash = new ModelProvenanceElement();
        elementWithNoHash.setFingerprint(missingHashManifest);
        ModelProvenanceElement elementWithMissingFile = new ModelProvenanceElement();
        elementWithMissingFile.setFingerprint(missingFileManifest);

        Map<String, ProvenanceVerification> validVerificationMap = new HashMap<>();
        validVerificationMap.put(file0, VERIFIED);
        validVerificationMap.put(file1, VERIFIED);
        Map<String, ProvenanceVerification> invalidVerificationMap = new HashMap<>();
        invalidVerificationMap.put(file0, VERIFIED);
        invalidVerificationMap.put(file1, INVALID);
        Map<String, ProvenanceVerification> noHashVerificationMap = new HashMap<>();
        noHashVerificationMap.put(file0, VERIFIED);
        noHashVerificationMap.put(file1, NO_HASH_AVAILABLE);
        Map<String, ProvenanceVerification> missingFileVerificationMap = new HashMap<>();
        missingFileVerificationMap.put(file0, VERIFIED);
        missingFileVerificationMap.put(file1, ID_NOT_FOUND);

        Map<String, File> validFilesHashMap = new HashMap<>();
        validFilesHashMap.put(file0, new File(ClassLoader.getSystemClassLoader().getResource("emptyDefinition.tosca").toURI()));
        validFilesHashMap.put(file1, new File(ClassLoader.getSystemClassLoader().getResource("secondDefinition.tosca").toURI()));

        return Stream.of(
            Arguments.of(
                validVerificationMap,
                element,
                validFilesHashMap,
                "Expects that all files are validated"
            ),
            Arguments.of(
                invalidVerificationMap,
                invalidElement,
                validFilesHashMap,
                "Expects that one file is invalid"
            ),
            Arguments.of(
                noHashVerificationMap,
                elementWithNoHash,
                validFilesHashMap,
                "Expects one file with no hash available"
            ),
            Arguments.of(
                missingFileVerificationMap,
                elementWithMissingFile,
                validFilesHashMap,
                "Expects a missing file in the manifest"
            )
        );
    }

    private static Stream<Arguments> fileUploadToImmutableStorageArguments() throws Exception {
        Map<String, InputStream> validFilesHashMap = new HashMap<>();
        validFilesHashMap.put("myTestFile.tosca", ClassLoader.getSystemClassLoader().getResource("emptyDefinition.tosca").openStream());
        validFilesHashMap.put("myTestFile2.tosca", ClassLoader.getSystemClassLoader().getResource("secondDefinition.tosca").openStream());
        validFilesHashMap.put("Tosca.meta", ClassLoader.getSystemClassLoader().getResource("Tosca.meta").openStream());

        return Stream.of(
            Arguments.of(
                validFilesHashMap,
                "Upload set of files to immutable file storage."
            )
        );
    }
}
