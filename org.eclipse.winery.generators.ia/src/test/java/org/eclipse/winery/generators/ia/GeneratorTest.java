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
        TInterface i = new TInterface();
        i.setName("http://www.example.org/interfaces/lifecycle");

        TOperation op;
        TOperation.InputParameters input;
        TOperation.OutputParameters output;
        TParameter param;

        op = new TOperation();
        op.setName("install");
        i.getOperation().add(op);

        input = new TOperation.InputParameters();
        param = new TParameter();
        param.setName("VMIP");
        param.setType("xs:string");
        input.getInputParameter().add(param);
        param = new TParameter();
        param.setName("DBMSUsername");
        param.setType("xs:string");
        input.getInputParameter().add(param);
        op.setInputParameters(input);

        output = new TOperation.OutputParameters();
        param = new TParameter();
        param.setName("Output");
        param.setType("xs:string");
        output.getOutputParameter().add(param);
        op.setOutputParameters(output);

        op = new TOperation();
        op.setName("uninstall");
        i.getOperation().add(op);

        input = new TOperation.InputParameters();
        param = new TParameter();
        param.setName("SomeLongParameterName");
        param.setType("xs:string");
        input.getInputParameter().add(param);
        param = new TParameter();
        param.setName("Port");
        param.setType("xs:string");
        input.getInputParameter().add(param);
        op.setInputParameters(input);

        op.setOutputParameters(output);

        Generator gen = new Generator(i, "org.opentosca.ia.test", new URL("http://test.com"), "TestMultipleOpsWithInOutParams", wd.toFile());
        gen.generateProject();
    }

    @Test
    public void testOneOpNoParams() throws Exception {
        TInterface i = new TInterface();
        i.setName("http://www.example.org/interfaces/lifecycle");

        TOperation op = new TOperation();
        op.setName("install");
        i.getOperation().add(op);

        Generator gen = new Generator(i, "org.opentosca.ia.test", new URL("http://test.com"), "TestOneOpNoParams", wd.toFile());
        gen.generateProject();
    }
}
