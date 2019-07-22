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

package org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer;

import org.eclipse.winery.tools.deployablecomponents.commons.Component;

import java.util.ArrayList;
import java.util.List;

public class ChmodAnalyzer implements CommandAnalyzer {
    public List<Component> analyze(String command) {
        // if the command do not contain any '+' it does not add any execute-right
        if (!command.contains("+")) {
            return new ArrayList<>();
        }
        command = command.replace(Commands.Chmod.asString(), "");
        String[] words = command.split("\\s");
        List<String> packages = new ArrayList<>();

        if (words[0].contains("+") && words[0].contains("x")) {
            packages.add(words[1]);
        }

        boolean execFound = false;
        for (String word : words) {
            if (word.length() > 0 && word.charAt(0) != '-') {
                continue;
            }
            if (execFound) {
                packages.add(word);
            }
            if (word.contains("+") && word.contains("x")) {
                execFound = true;
            }
        }
        return parseComponents(packages);
    }

    private List<Component> parseComponents(List<String> packages) {
        List<Component> components = new ArrayList<>();
        for (String softwarePackage : packages) {
            String version = "undefined";
            String name = softwarePackage;
            components.add(new Component(name, version, "undefined"));
        }
        return components;
    }

}
