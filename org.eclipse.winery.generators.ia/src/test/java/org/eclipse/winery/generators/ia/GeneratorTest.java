/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.generators.ia;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GeneratorTest {

    private static Path wd;

    @BeforeAll
    public static void initialize() throws Exception {
        wd = Files.createTempDirectory("IAGenerator");
    }

    @AfterAll
    public static void destroy() throws Exception {
        FileUtils.forceDelete(wd.toFile());
    }

    @Test
    public void testMultipleOpsWithInOutParams() throws Exception {
        TOperation install = new TOperation.Builder("install")
            .addInputParameter(
                new TParameter.Builder("VMIP", "xs:string").build()
            )
            .addInputParameter(
                new TParameter.Builder("DBMSUsername", "xs:string").build()
            )
            .addOutputParameter(
                new TParameter.Builder("Output", "xs:string").build()
            )
            .build();

        TOperation uninstall = new TOperation.Builder("uninstall")
            .addInputParameter(
                new TParameter.Builder("SomeLongParameterName", "xs:string").build()
            )
            .addInputParameter(
                new TParameter.Builder("Port", "xs:string").build()
            )
            .addOutputParameter(
                new TParameter.Builder("Output", "xs:string").build()
            )
            .build();

        TInterface iFace = new TInterface.Builder(
            "http://www.example.org/interfaces/lifecycle",
            Arrays.asList(
                install,
                uninstall
            )
        ).build();

        Generator gen = new Generator(iFace,
            "org.opentosca.ia.test",
            new URL("http://test.com"),
            "TestMultipleOpsWithInOutParams",
            wd.toFile()
        );
        gen.generateProject();
    }

    @Test
    public void testOneOpNoParams() throws Exception {
        TInterface iFace = new TInterface.Builder(
            "http://www.example.org/interfaces/lifecycle",
            Collections.singletonList(
                new TOperation.Builder("install").build()
            )
        ).build();

        Generator gen = new Generator(iFace,
            "org.opentosca.ia.test",
            new URL("http://test.com"),
            "TestOneOpNoParams",
            wd.toFile()
        );
        gen.generateProject();
    }
}
