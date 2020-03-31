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

package org.eclipse.winery.repository.importing;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.winery.accountability.model.ProvenanceVerification;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// todo try to migrate to new accountability testing scheme (ganache with resets!)
@Disabled
//@EnabledIf("(new java.io.File(\"C:/Ethereum/keystore/UTC--2018-03-05T15-33-22.456000000Z--e4b51a3d4e77d2ce2a9d9ce107ec8ec7cff5571d.json\").exists())")
class CsarImporterValidationTest extends TestWithGitBackedRepository {

    // Set explicitly to revision bbb39cdb32cbcf38bd691abe6a66ae62f4aba428 because the service template
    // {http://plain.winery.opentosca.org/servicetemplates}ServiceTemplateWithAllReqCapVariants was exported in this state and was
    // stored in transaction "0x169ea33d242749c80ce21be5b52343c7c56c4fb1e2067209217da7385b3eb72a" in the ethereum testnet. The
    // transaction  is available via https://rinkeby.etherscan.io/tx/0x169ea33d242749c80ce21be5b52343c7c56c4fb1e2067209217da7385b3eb72a

    private CsarImporter csarImporter;

    @BeforeEach
    void setUp() throws Exception {
        this.setRevisionTo("bbb39cdb32cbcf38bd691abe6a66ae62f4aba428");
        csarImporter = new CsarImporter(repository);
    }

    @Test
    public void testImportAndValidation() throws Exception {
        try (InputStream input = new FileInputStream("src/test/resources/csar/ServiceTemplateWithAllReqCapVariants.csar")) {

            CsarImportOptions options = new CsarImportOptions();
            options.setOverwrite(true);
            options.setAsyncWPDParsing(false);
            options.setValidate(true);
            ImportMetaInformation importMetaInformation = csarImporter.readCSAR(input, options);

            assertEquals(ProvenanceVerification.VERIFIED, importMetaInformation.verificationMap.remove("TOSCA-Metadata/TOSCA.meta"));

            for (Map.Entry<String, ProvenanceVerification> entry : importMetaInformation.verificationMap.entrySet()) {
                assertEquals(ProvenanceVerification.VERIFIED, entry.getValue());
            }
        }
    }

    @Test
    public void testImportAndInvalidValidation() throws Exception {
        try (InputStream inputStream = new FileInputStream("src/test/resources/csar/ServiceTemplateWithAllReqCapVariants-Invalid.csar")) {

            CsarImportOptions options = new CsarImportOptions();
            options.setOverwrite(true);
            options.setAsyncWPDParsing(false);
            options.setValidate(true);
            ImportMetaInformation importMetaInformation = csarImporter.readCSAR(inputStream, options);

            ProvenanceVerification invalidDefinition = importMetaInformation.verificationMap
                .remove("Definitions/servicetemplates__ServiceTemplateWithAllReqCapVariants.tosca");
            assertEquals(ProvenanceVerification.INVALID, invalidDefinition);

            for (Map.Entry<String, ProvenanceVerification> entry : importMetaInformation.verificationMap.entrySet()) {
                assertEquals(ProvenanceVerification.VERIFIED, entry.getValue());
            }
        }
    }

    @Test
    public void testImportOfCsarWithoutAuthorship() throws Exception {
        try (InputStream inputStream = new FileInputStream("src/test/resources/csar/ProvenanceCsarWithoutAuthorizedAuthors_w1-wip1.csar")) {

            CsarImportOptions options = new CsarImportOptions();
            options.setOverwrite(true);
            options.setAsyncWPDParsing(false);
            options.setValidate(true);
            ImportMetaInformation importMetaInformation = csarImporter.readCSAR(inputStream, options);

            ProvenanceVerification verification = importMetaInformation.verificationMap.get("TOSCA-Metadata/TOSCA.meta");

            assertEquals(ProvenanceVerification.NO_AUTHORIZATION_DATA_AVAILABLE, verification);
        }
    }
}
