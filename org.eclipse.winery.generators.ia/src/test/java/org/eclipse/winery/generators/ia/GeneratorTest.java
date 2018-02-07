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

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class GeneratorTest {

    private static Path WORKING_DIR;

    @BeforeClass
    public static void initializeWorkingDir() throws Exception {
        WORKING_DIR = Files.createTempDirectory("IAGenerator");
    }

    @Test
    public void testInOut() throws Exception {
        TInterface tinterface = new TInterface();
        tinterface.setName("http://www.example.org/interfaces/lifecycle");

        TOperation op1 = new TOperation();
        op1.setName("Op1InOut");
        tinterface.getOperation().add(op1);
        TOperation.InputParameters op1InputParameters = new TOperation.InputParameters();

        TParameter op1ip1 = new TParameter();
        op1ip1.setName("op1ip1");
        op1ip1.setType("xs:string");
        op1InputParameters.getInputParameter().add(op1ip1);
        TParameter op1ip2 = new TParameter();
        op1ip2.setName("op1ip2");
        op1ip2.setType("xs:string");
        op1InputParameters.getInputParameter().add(op1ip2);
        op1.setInputParameters(op1InputParameters);

        TOperation.OutputParameters op1OutputParameters = new TOperation.OutputParameters();
        TParameter op1op1 = new TParameter();
        op1op1.setName("op1op1");
        op1op1.setType("xs:string");
        op1OutputParameters.getOutputParameter().add(op1op1);
        TParameter op1op2 = new TParameter();
        op1op2.setName("op1op2");
        op1op1.setType("xs:string");
        op1OutputParameters.getOutputParameter().add(op1op2);
        op1.setOutputParameters(op1OutputParameters);

        TNodeType nodeType = new TNodeType();
        nodeType.setName("test");
        nodeType.setTargetNamespace("http://asd.com");

        Generator gen = new Generator(tinterface, "org.opentosca.ia", new URL("http://asd.com"), "testname", WORKING_DIR.toFile());
        Path generateProject = gen.generateProject();
        System.out.println(generateProject);
    }

    @Test
    public void testMultipleOperationsInOrOut() throws Exception {
        TInterface tinterface = new TInterface();
        tinterface.setName("TestInOrOut");

        TOperation opIn = new TOperation();
        opIn.setName("OpIn");
        tinterface.getOperation().add(opIn);

        TOperation.InputParameters op1InputParameters = new TOperation.InputParameters();
        TParameter op1ip1 = new TParameter();
        op1ip1.setName("op1ip1");
        op1ip1.setType("xs:string");
        op1InputParameters.getInputParameter().add(op1ip1);
        TParameter op1ip2 = new TParameter();
        op1ip2.setName("op1ip2");
        op1ip2.setType("xs:string");
        op1InputParameters.getInputParameter().add(op1ip2);
        opIn.setInputParameters(op1InputParameters);

        TOperation opOut = new TOperation();
        opOut.setName("OpOut");
        tinterface.getOperation().add(opOut);

        TOperation.OutputParameters op1OutputParameters = new TOperation.OutputParameters();
        TParameter op1op1 = new TParameter();
        op1op1.setName("op1op1");
        op1op1.setType("xs:string");
        op1OutputParameters.getOutputParameter().add(op1op1);
        TParameter op1op2 = new TParameter();
        op1op2.setName("op1op2");
        op1op1.setType("xs:string");
        op1OutputParameters.getOutputParameter().add(op1op2);
        opOut.setOutputParameters(op1OutputParameters);

        TNodeType nodeType = new TNodeType();
        nodeType.setName("test");
        nodeType.setTargetNamespace("http://asd.com");

        Generator gen = new Generator(tinterface, "org.opentosca.ia", new URL("http://asd.com"), "testname", WORKING_DIR.toFile());
        Path generateProject = gen.generateProject();
        System.out.println(generateProject);
    }

    @Test
    public void testNoParams() throws Exception {
        TInterface tinterface = new TInterface();
        tinterface.setName("TestNoParams");

        TOperation opIn = new TOperation();
        opIn.setName("OpNoParams");
        tinterface.getOperation().add(opIn);

        TNodeType nodeType = new TNodeType();
        nodeType.setName("test");
        nodeType.setTargetNamespace("http://asd.com");

        Generator gen = new Generator(tinterface, "org.opentosca.ia", new URL("http://asd.com"), "testname", WORKING_DIR.toFile());
        Path generateProject = gen.generateProject();
        System.out.println(generateProject);
    }

}
