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

package org.eclipse.winery.tools.deployablecomponents.fileanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.tools.deployablecomponents.commons.Component;
import org.eclipse.winery.tools.deployablecomponents.commons.Dockerfile;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.ApkAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.AptgetAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.ChmodAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.CommandAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.NpmAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.Pip3Analyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.PipAnalyzer;
import org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer.YumAnalyzer;

import org.apache.commons.lang3.tuple.Pair;

/* Analyzes a dockerfile by using command analyzers dependent on the included commands in the file
 */
public class Fileanalyzer {

    private Filesplitter filesplitter;

    public Fileanalyzer() {
        filesplitter = new Filesplitter();
    }

    public List<Pair<Component, List<Component>>> analyseDockerfile(Dockerfile dockerfile) {
        List<String> lines = filesplitter.splitDockerfile(dockerfile);
        boolean isFirst = true;
        List<String> linesToAnalyze = new ArrayList<>();
        List<Pair<Component, List<Component>>> results = new ArrayList<>();
        // split Dockerfile into parts (multi-stage builds)
        for (String nextLine : lines) {
            // if the next line is a FROM line, the previous Dockerfile-part is finished (except its the first FROM)
            if (nextLine.toUpperCase().indexOf(Dockerfile.DockerInstruction.FROM.asString()) == 0) {
                if (!isFirst) {
                    Component baseComponent = extractBaseComponent(linesToAnalyze);
                    List<Component> topComponents = extractTopComponents(linesToAnalyze);
                    if (baseComponent == null || topComponents.isEmpty()) {
                        continue;
                    }
                    results.add(Pair.of(baseComponent, topComponents));
                    linesToAnalyze.clear();
                }
                isFirst = false;
            }
            linesToAnalyze.add(nextLine);
        }

        // at the end, analyze the last Dockerfile-part
        Component baseComponent = extractBaseComponent(linesToAnalyze);
        List<Component> topComponents = extractTopComponents(linesToAnalyze);
        if (baseComponent == null || topComponents.isEmpty()) {
            return results;
        }
        results.add(Pair.of(baseComponent, topComponents));

        return results;
    }

    // find a line identified by a dockerfile instruction
    private List<String> findLines(List<String> lines, Dockerfile.DockerInstruction instruction) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line.toUpperCase().indexOf(instruction.asString()) == 0) {
                result.add(line);
            }
        }
        return result;
    }

    // find the first line with an occurrence of a defined dockerfile instruction
    private int findFirstLineNumber(List<String> lines, Dockerfile.DockerInstruction instruction) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).indexOf(instruction.asString()) == 0) {
                return i;
            }
        }
        return -1;
    }

    // possible syntax for FROM line:
    // FROM <image> [AS <name>]
    // FROM <image>[:<tag>] [AS <name>]
    // FROM <image>[@<digest>] [AS <name>]
    // see docs.docker.com
    private Component extractBaseComponent(List<String> lines) {
        List<String> foundLines = findLines(lines, Dockerfile.DockerInstruction.FROM);
        if (foundLines.isEmpty()) {
            return null;
        }

        String fromLine = foundLines.get(0);
        fromLine = fromLine.substring(4);
        if (fromLine.contains(" AS ")) {
            fromLine = fromLine.substring(0, fromLine.indexOf(" AS "));
        }
        fromLine = fromLine.trim();

        if (fromLine.contains("$")) {
            List<String> argLines = new ArrayList<>();
            // only take lines before FROM line (docker behaviour)
            int indexOfFromLine = findFirstLineNumber(lines, Dockerfile.DockerInstruction.FROM);
            for (int i = 0; i < indexOfFromLine; i++) {
                argLines.add(lines.get(i));
            }
            List<String> argEnvLines = findLines(argLines, Dockerfile.DockerInstruction.ARG);
            argEnvLines.addAll(findLines(argLines, Dockerfile.DockerInstruction.ENV));
            fromLine = replaceArg(fromLine, argEnvLines);
        }

        if (fromLine.contains(":")) {
            return new Component(fromLine.substring(0, fromLine.indexOf(':')), fromLine.substring(fromLine.indexOf(':') + 1), "equals");
        }
        if (fromLine.contains("@")) {
            return new Component(fromLine.substring(0, fromLine.indexOf('@')), fromLine.substring(fromLine.indexOf('@') + 1), "equals");
        }
        return new Component(fromLine, "undefined", "undefined");
    }

    // replace argument variables which have a default value with their value
    private String replaceArg(String line, List<String> argLines) {
        while (line.contains("$")) {
            int indexStart = line.indexOf("$");
            int indexEnd = line.substring(indexStart).indexOf(" ");
            if (indexEnd < 0) {
                indexEnd = line.length();
            } else {
                // only the substring starting at indexStart is searched for the next whitespace,
                // therefore indexStart have to be added
                indexEnd += indexStart;
            }
            String variableName = line.substring(indexStart + 1, indexEnd);
            if (variableName.contains("{")) {
                variableName = variableName.substring(1, variableName.length() - 1);
            }

            String argValue = "";
            for (String argLine : argLines) {
                // ARG var might not have a default value
                if (argLine.contains("=")) {
                    String argName = argLine.substring(4, argLine.indexOf("="));
                    if (argName.equals(variableName)) {
                        argValue = argLine.substring(argLine.indexOf("=") + 1);
                    }
                }
            }

            line = line.replace(line.substring(indexStart, indexEnd), argValue);
        }
        return line;
    }

    private List<Component> extractTopComponents(List<String> lines) {
        List<String> runLines = findLines(lines, Dockerfile.DockerInstruction.RUN);
        return splitLinesIntoComponents(runLines);
    }

    // extract a list of all software components from multiple lines
    private List<Component> splitLinesIntoComponents(List<String> lines) {
        List<Component> commands = new ArrayList<>();

        for (String line : lines) {
            List<String> cmdLines = new ArrayList<>();
            while (line.contains(" && ")) {
                cmdLines.add(line.substring(0, line.indexOf(" && ")));
                line = line.replace(cmdLines.get(cmdLines.size() - 1) + " && ", "");
            }
            cmdLines.add(line);

            for (String cmd : cmdLines) {
                if (cmd.contains("$")) {
                    List<String> argLines = findLines(lines, Dockerfile.DockerInstruction.ARG);
                    cmd = replaceArg(cmd, argLines);
                }

                if (cmd.contains(CommandAnalyzer.Commands.AptGet.asString())) {
                    commands.addAll(new AptgetAnalyzer().analyze(cmd));
                } else if (cmd.contains(CommandAnalyzer.Commands.Pip3.asString())) {
                    commands.addAll(new Pip3Analyzer().analyze(cmd));
                } else if (cmd.contains(CommandAnalyzer.Commands.Pip.asString())) {
                    commands.addAll(new PipAnalyzer().analyze(cmd));
                } else if (cmd.contains(CommandAnalyzer.Commands.Yum.asString())) {
                    commands.addAll(new YumAnalyzer().analyze(cmd));
                } else if (cmd.contains(CommandAnalyzer.Commands.Npm.asString())) {
                    commands.addAll(new NpmAnalyzer().analyze(cmd));
                } else if (cmd.contains(CommandAnalyzer.Commands.Apk.asString())) {
                    commands.addAll(new ApkAnalyzer().analyze(cmd));
                } else if (cmd.contains(CommandAnalyzer.Commands.Chmod.asString())) {
                    commands.addAll(new ChmodAnalyzer().analyze(cmd));
                }
            }
        }

        return commands;
    }
}
