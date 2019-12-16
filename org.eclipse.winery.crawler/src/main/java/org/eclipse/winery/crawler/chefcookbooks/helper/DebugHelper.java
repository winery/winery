/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.crawler.chefcookbooks.helper;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.eclipse.winery.crawler.chefcookbooks.chefdslparser.ChefDSLParser;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * This class provides methods for debugging.
 */
public class DebugHelper {

    /**
     * Show abstract syntax tree in a JFrame.
     *
     * @param parser ChefDSLParser the tree was generated with.
     * @param tree   Abstract Syntax Tree of the parsed file.
     */
    public static void showChefdslAst(ChefDSLParser parser, ParseTree tree) {
        JFrame frame = new JFrame("Antlr AST");
        JPanel panel = new JPanel();
        JScrollPane pane = new JScrollPane(panel);
        TreeViewer viewr = new TreeViewer(Arrays.asList(
            parser.getRuleNames()), tree);
        viewr.setScale(1.5);//scale a little
        panel.add(viewr);
        frame.add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        frame.setVisible(true);
    }
}
