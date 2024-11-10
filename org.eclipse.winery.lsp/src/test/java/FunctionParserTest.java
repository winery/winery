/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.lsp.Server.ServerCore.TOSCAFunctions.FunctionParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionParserTest {

    private FunctionParser functionParser;

    @BeforeEach
    public void setUp() {
        functionParser = new FunctionParser();
    }

    @Test
    public void testParseFunctionCallGreaterOrEqual() {
        functionParser.parseFunctionCall("{ $greater_or_equal: [ $value, 9 ] }");
        Stack<Map<String, List<String>>> stack = functionParser.getFunctionStack();
        assertFalse(stack.isEmpty());
        Map<String, List<String>> top = stack.peek();
        assertEquals(1, top.size());
        assertTrue(top.containsKey("$value"));
        stack.pop();
        top = stack.peek();
        assertTrue(top.containsKey("$greater_or_equal"));
        assertEquals(Arrays.asList("$value","9"), top.get("$greater_or_equal"));
    }

    @Test
    public void testParseFunctionCallLessThan() {
        functionParser.parseFunctionCall("{ $less_than: [ $value, 5 ] }");
        Stack<Map<String, List<String>>> stack = functionParser.getFunctionStack();
        assertFalse(stack.isEmpty());
        Map<String, List<String>> top = stack.peek();
        assertEquals(1, top.size());
        assertTrue(top.containsKey("$value"));
        stack.pop();
        top = stack.peek();
        assertTrue(top.containsKey("$less_than"));
        assertEquals(Arrays.asList("$value","5"), top.get("$less_than"));
    }
    
    @Test
    public void testParseFunctionCallLessOrEqual16() {
        functionParser.parseFunctionCall("{ $less_or_equal: [ $value, 16 ] }");
        Stack<Map<String, List<String>>> stack = functionParser.getFunctionStack();
        assertFalse(stack.isEmpty());
        Map<String, List<String>> top = stack.peek();
        assertEquals(1, top.size());
        assertTrue(top.containsKey("$value"));
        stack.pop();
        top = stack.peek();
        assertTrue(top.containsKey("$less_or_equal"));
        assertEquals(Arrays.asList("$value","16"), top.get("$less_or_equal"));
    }
    
}
