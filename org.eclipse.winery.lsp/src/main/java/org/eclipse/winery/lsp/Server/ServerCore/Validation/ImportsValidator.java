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

package org.eclipse.winery.lsp.Server.ServerCore.Validation;

import com.google.common.collect.LinkedHashMultimap;
import org.eclipse.winery.lsp.Server.ServerAPI.API.context.LSContext;
import org.eclipse.winery.lsp.Server.ServerCore.Parsing.TOSCAFileParser;
import org.eclipse.winery.lsp.Server.ServerCore.Utils.CommonUtils;
import org.yaml.snakeyaml.error.Mark;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportsValidator implements DiagnosesHandler {
    public ArrayList<DiagnosticsSetter> diagnostics = new ArrayList<>();
    private LSContext context;
    
    public ImportsValidator(LSContext context) {
     this.context = context;
    }

    public ArrayList<DiagnosticsSetter> validateImports(List<Object> importsList, String yamlContent, Map<String, Object> yamlMap, Map<String, Mark> positions, String[] lines) {
        Set<String> validImportsKeywords = Set.of(
            "url", "profile", "repository", "namespace", "description", "metadata"  
        );
        context.getImportedToscaFiles().get(context.getCurrentToscaFilePath()).clear();
        context.getNamespaceDefinitions().get(context.getCurrentToscaFilePath()).clear();
        int i = 0;
        for (Object importElement: importsList) {
            String importsPath = "imports[" + i  + "]";
            i++;
            if (importElement instanceof Map) {
                for (String importsKey : ((Map<String, Object>) importElement).keySet()) {
                    if (!validImportsKeywords.contains(importsKey)) {
                        Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + importsKey);
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                        handleNotValidKeywords("Invalid imports keyword: " + importsKey + " at line " + line + ", column " + column, line, column, endColumn);
                    }
                    if (((Map<?, ?>) importElement).containsKey("url") && ((Map<?, ?>) importElement).containsKey("profile") ) {
                        Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "url");
                        int line = mark != null ? mark.getLine() + 1 : -1;
                        int column = mark != null ? mark.getColumn() + 1 : -1;
                        int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                        handleNotValidKeywords("Import statement must include either a url or a profile, but not both.", line, column, endColumn);
    
                        mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "profile");
                        line = mark != null ? mark.getLine() + 1 : -1;
                        column = mark != null ? mark.getColumn() + 1 : -1;
                        endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                        handleNotValidKeywords("Import statement must include either a url or a profile, but not both.", line, column, endColumn);
                     } else if (((Map<?, ?>) importElement).get(importsKey) != null && importsKey.equals("url")) {
                        validateURL(yamlContent, yamlMap, positions,lines, (Map<?, ?>) importElement, importsKey, importsPath);
                    } else if (((Map<?, ?>) importElement).get(importsKey) != null && importsKey.equals("profile")) {
                        validateProfile(yamlContent, lines, (Map<?, ?>) importElement, importsKey, importsPath);
                    }
                }
                if (((Map<?, ?>) importElement).containsKey("repository") && !((Map<?, ?>) importElement).containsKey("url") ) {
                    Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "repository");
                    int line = mark != null ? mark.getLine() + 1 : -1;
                    int column = mark != null ? mark.getColumn() + 1 : -1;
                    int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                    handleNotValidKeywords("The repository name can only be used when a url is specified.", line, column, endColumn);
                }
            } else {
                Mark mark = context.getContextDependentConstructorPositions().get("imports");
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                handleNotValidKeywords("Invalid imports", line, column, endColumn);
            }
        }
        return diagnostics;
    }

    private void validateProfile(String yamlContent, String[] lines, Map<?, ?> importElement, String importsKey, String importsPath) {
        if (importElement.get(importsKey) instanceof String profileValue) {
            TOSCAFileParser toscaFileParser = new TOSCAFileParser();
            try {
                boolean isFileExist = false;
                for (Path ToscaFilePath: context.getDirectoryFilePaths()) {
                    if (CommonUtils.isToscaFile(ToscaFilePath)) {
                        Map<String, Object> newyamlMap = toscaFileParser.ParseTOSCAFile(ToscaFilePath,context.getClient());
                    TOSCAFileValidator toscaFileValidator = new TOSCAFileValidator();

                    if (toscaFileParser.getToscaFile() != null && toscaFileParser.getToscaFile().profile().isPresent() && toscaFileParser.getToscaFile().profile().get().getValue().equals(profileValue)) {
                            LSContext newContext = context.clone();
                            newContext.setCotextDependentPositions(toscaFileParser.getContextDependentConstructorPositions());
                            newContext.setCurrentToscaFile(toscaFileParser.getToscaFile());
                            newContext.setImportedToscaFiles(LinkedHashMultimap.create());
                            newContext.setNamespaceDefinitions(LinkedHashMultimap.create());
                            newContext.setCurrentToscaFilePath(ToscaFilePath);
                           
                            Path directoryPath = ToscaFilePath.getParent();
                            // Determine all parent directories of all files
                            Stream<Path> walk = Files.walk(directoryPath);
                            Set<Path> filePaths = walk.filter(Files::isRegularFile)
                                .flatMap(path -> {
                                    List<Path> additions = new ArrayList<>();
                                    do {
                                        additions.add(path);
                                        path = path.getParent();
                                    } while (path != null);
                                    return additions.stream();
                                })
                                .collect(Collectors.toSet());
                            newContext.getDirectoryFilePaths().clear();
                            newContext.setDirectoryFilePaths(filePaths);
                            toscaFileValidator.validate(newyamlMap, newContext, toscaFileParser.getYamlContent(), toscaFileParser.getConstructorPositions());
                        context.getImportedToscaFiles().put(context.getCurrentToscaFilePath() ,Map.of(profileValue,newContext.getCurrentToscaFile()));
                        isFileExist = true;
                        if (importElement.get("namespace") != null) {
                            if (importElement.get("namespace") instanceof String namespace) {
                                context.getNamespaceDefinitions().put(context.getCurrentToscaFilePath() ,Map.of(namespace, newContext.getCurrentToscaFile()));
                            }
                        }
                        break;
                    }
                }
                }
                if (!isFileExist) {
                    Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "profile" + "." + importElement.get(importsKey));
                    int line = mark != null ? mark.getLine() + 1 : -1;
                    int column = mark != null ? mark.getColumn() + 1 : -1;
                    int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);
                    handleNotValidKeywords("Tosca File not found.", line, column, endColumn); 
                }
            } catch (Exception e) {
                Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "profile");
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
                handleNotValidKeywords(e.getMessage(), line, column, endColumn);
            }
        } else {
            Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "profile");
            int line = mark != null ? mark.getLine() + 1 : -1;
            int column = mark != null ? mark.getColumn() + 1 : -1;
            int endColumn = CommonUtils.getEndColumn(yamlContent, line, column, lines);
            handleNotValidKeywords("Profile value should be string", line, column, endColumn);
        }
    }

    private void validateURL(String yamlContent, Map<String, Object> yamlMap, Map<String, Mark> positions, String[] lines, Map<?, ?> importElement, String importsKey, String importsPath) {
        if (importElement.get(importsKey) instanceof String url) {
            Path currentFilePath = context.getCurrentToscaFilePath();
            TOSCAFileParser toscaFileParser = new TOSCAFileParser();
            TOSCAFileValidator toscaFileValidator = new TOSCAFileValidator();
            try {
                Path ImportedToscaFilePath = currentFilePath.getParent().resolve(url);
                if (CommonUtils.isToscaFile(ImportedToscaFilePath)) {
                    Map<String, Object> newyamlMap = toscaFileParser.ParseTOSCAFile(ImportedToscaFilePath,context.getClient());
                        LSContext newContext = context.clone();
                        newContext.setCotextDependentPositions(toscaFileParser.getContextDependentConstructorPositions());
                        newContext.setCurrentToscaFile(toscaFileParser.getToscaFile());
                        newContext.setImportedToscaFiles(LinkedHashMultimap.create());
                        newContext.setNamespaceDefinitions(LinkedHashMultimap.create());
                        newContext.setCurrentToscaFilePath(ImportedToscaFilePath);

                        Path directoryPath = ImportedToscaFilePath.getParent();
                        // Determine all parent directories of all files
                        Stream<Path> walk = Files.walk(directoryPath);
                        Set<Path> filePaths = walk.filter(Files::isRegularFile)
                            .flatMap(path -> {
                                List<Path> additions = new ArrayList<>();
                                do {
                                    additions.add(path);
                                    path = path.getParent();
                                } while (path != null);
                                return additions.stream();
                            })
                            .collect(Collectors.toSet());
                        newContext.getDirectoryFilePaths().clear();
                        newContext.setDirectoryFilePaths(filePaths);
                        toscaFileValidator.validate(newyamlMap, newContext, toscaFileParser.getYamlContent(), toscaFileParser.getConstructorPositions());
                    context.getToscaFilesPath().put(currentFilePath, newContext.getCurrentToscaFile());
                    context.getImportedToscaFiles().put(currentFilePath ,Map.of(url,newContext.getCurrentToscaFile()));
                    if (importElement.get("namespace") != null) {
                    if (importElement.get("namespace") instanceof String namespace) {
                        context.getNamespaceDefinitions().put(context.getCurrentToscaFilePath() ,Map.of(namespace,newContext.getCurrentToscaFile()));
                    }
                }
                }
            } catch (Exception e) {
                Mark mark = context.getContextDependentConstructorPositions().get(importsPath + "." + "url" + "." + url);
                int line = mark != null ? mark.getLine() + 1 : -1;
                int column = mark != null ? mark.getColumn() + 1 : -1;
                int endColumn = CommonUtils.getEndColumnForValueError(yamlContent, line, column, lines);
                handleNotValidKeywords("The error message, " + e.getMessage(), line, column, endColumn);
            }
        }
    }

    @Override
    public void handleNotValidKeywords(String message, int line, int column, int endColumn) {
        DiagnosticsSetter ArtifactTypeDiagnostic = new DiagnosticsSetter();
        ArtifactTypeDiagnostic.setErrorMessage(message);
        ArtifactTypeDiagnostic.setErrorContext("Not Valid Keywords");
        ArtifactTypeDiagnostic.setErrorColumn(column);
        ArtifactTypeDiagnostic.setErrorEndColumn(endColumn);
        ArtifactTypeDiagnostic.setErrorLine(line);
        diagnostics.add(ArtifactTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, Path path) {
        DiagnosticsSetter ArtifactTypeDiagnostic = new DiagnosticsSetter();
        ArtifactTypeDiagnostic.setErrorMessage(message);
        ArtifactTypeDiagnostic.setErrorContext("Parsing Error");
        try {
            long lineCount = Files.lines(path).count();
            ArtifactTypeDiagnostic.setErrorLine((int) lineCount);
        } catch (IOException e) {
            ArtifactTypeDiagnostic.setErrorLine(-1);
        }
        ArtifactTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(ArtifactTypeDiagnostic);
    }

    @Override
    public void handleDiagnosticsError(String message, String content) {
        DiagnosticsSetter ArtifactTypeDiagnostic = new DiagnosticsSetter();
        ArtifactTypeDiagnostic.setErrorMessage(message);
        ArtifactTypeDiagnostic.setErrorContext("Parsing Error");
        ArtifactTypeDiagnostic.setErrorLine(countLines(content));
        ArtifactTypeDiagnostic.setErrorColumn(1);
        diagnostics.add(ArtifactTypeDiagnostic);
    }

    private int countLines(String content) {
        return (int) content.lines().count();
    }

}
