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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PythonGeneratorTest {

    @TempDir

    private Path wd;

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

        PythonGenerator gen = new PythonGenerator(iFace,
            "interface",
            wd);
        Assertions.assertThrows(IllegalArgumentException.class, gen::generateArtifact);
    }

    @Test
    public void testOneOpNoParams() throws Exception {
        TInterface iFace = new TInterface.Builder(
            "http://www.example.org/interfaces/lifecycle",
            Collections.singletonList(
                new TOperation.Builder("install").build()
            )
        ).build();

        PythonGenerator gen = new PythonGenerator(iFace,
            "install",
            wd);
        gen.generateArtifact();
        Assertions.assertTrue(Files.exists(wd.resolve("README.md")));
        Assertions.assertTrue(Files.exists(wd.resolve("install.py")));
    }
}
