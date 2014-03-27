/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Tobias Binz - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.generators.ia;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.winery.model.tosca.ObjectFactory;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TOperation.InputParameters;
import org.eclipse.winery.model.tosca.TOperation.OutputParameters;
import org.eclipse.winery.model.tosca.TParameter;
import org.junit.BeforeClass;

public class Test {
	
	private static Path WORKING_DIR;
	
	
	@BeforeClass
	public static void initializeWorkingDir() throws IOException {
		Test.WORKING_DIR = Files.createTempDirectory("IAGenerator");
	}
	
	@org.junit.Test
	public void testInOut() throws MalformedURLException {
		ObjectFactory f = new ObjectFactory();
		
		TInterface tinterface = f.createTInterface();
		tinterface.setName("http://www.example.org/interfaces/lifecycle");
		
		TOperation op1 = f.createTOperation();
		op1.setName("Op1InOut");
		tinterface.getOperation().add(op1);
		InputParameters op1InputParameters = f.createTOperationInputParameters();
		
		TParameter op1ip1 = f.createTParameter();
		op1ip1.setName("op1ip1");
		op1ip1.setType("xs:string");
		op1InputParameters.getInputParameter().add(op1ip1);
		TParameter op1ip2 = f.createTParameter();
		op1ip2.setName("op1ip2");
		op1ip2.setType("xs:string");
		op1InputParameters.getInputParameter().add(op1ip2);
		op1.setInputParameters(op1InputParameters);
		
		OutputParameters op1OutputParameters = f.createTOperationOutputParameters();
		TParameter op1op1 = f.createTParameter();
		op1op1.setName("op1op1");
		op1op1.setType("xs:string");
		op1OutputParameters.getOutputParameter().add(op1op1);
		TParameter op1op2 = f.createTParameter();
		op1op2.setName("op1op2");
		op1op1.setType("xs:string");
		op1OutputParameters.getOutputParameter().add(op1op2);
		op1.setOutputParameters(op1OutputParameters);
		
		TNodeType nodeType = f.createTNodeType();
		nodeType.setName("test");
		nodeType.setTargetNamespace("http://asd.com");
		
		Generator gen = new Generator(tinterface, "org.opentosca.ia", new URL("http://asd.com"), "testname", Test.WORKING_DIR.toFile());
		File generateProject = gen.generateProject();
		System.out.println(generateProject);
	}
	
	@org.junit.Test
	public void testMultipleOperationsInOrOut() throws MalformedURLException {
		ObjectFactory f = new ObjectFactory();
		
		TInterface tinterface = f.createTInterface();
		tinterface.setName("TestInOrOut");
		
		TOperation opIn = f.createTOperation();
		opIn.setName("OpIn");
		tinterface.getOperation().add(opIn);
		
		InputParameters op1InputParameters = f.createTOperationInputParameters();
		TParameter op1ip1 = f.createTParameter();
		op1ip1.setName("op1ip1");
		op1ip1.setType("xs:string");
		op1InputParameters.getInputParameter().add(op1ip1);
		TParameter op1ip2 = f.createTParameter();
		op1ip2.setName("op1ip2");
		op1ip2.setType("xs:string");
		op1InputParameters.getInputParameter().add(op1ip2);
		opIn.setInputParameters(op1InputParameters);
		
		TOperation opOut = f.createTOperation();
		opOut.setName("OpOut");
		tinterface.getOperation().add(opOut);
		
		OutputParameters op1OutputParameters = f.createTOperationOutputParameters();
		TParameter op1op1 = f.createTParameter();
		op1op1.setName("op1op1");
		op1op1.setType("xs:string");
		op1OutputParameters.getOutputParameter().add(op1op1);
		TParameter op1op2 = f.createTParameter();
		op1op2.setName("op1op2");
		op1op1.setType("xs:string");
		op1OutputParameters.getOutputParameter().add(op1op2);
		opOut.setOutputParameters(op1OutputParameters);
		
		TNodeType nodeType = f.createTNodeType();
		nodeType.setName("test");
		nodeType.setTargetNamespace("http://asd.com");
		
		Generator gen = new Generator(tinterface, "org.opentosca.ia", new URL("http://asd.com"), "testname", Test.WORKING_DIR.toFile());
		File generateProject = gen.generateProject();
		System.out.println(generateProject);
	}
	
	@org.junit.Test
	public void testNoParams() throws MalformedURLException {
		ObjectFactory f = new ObjectFactory();
		
		TInterface tinterface = f.createTInterface();
		tinterface.setName("TestNoParams");
		
		TOperation opIn = f.createTOperation();
		opIn.setName("OpNoParams");
		tinterface.getOperation().add(opIn);
		
		TNodeType nodeType = f.createTNodeType();
		nodeType.setName("test");
		nodeType.setTargetNamespace("http://asd.com");
		
		Generator gen = new Generator(tinterface, "org.opentosca.ia", new URL("http://asd.com"), "testname", Test.WORKING_DIR.toFile());
		File generateProject = gen.generateProject();
		System.out.println(generateProject);
	}
	
}
