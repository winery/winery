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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);

    // Placeholder applicable for all files
    private static final String PLACEHOLDER_JAVA_PACKAGE = "IA_PACKAGE";
    private static final String PLACEHOLDER_NAMESPACE = "IA_NAMESPACE";
    private static final String PLACEHOLDER_CLASS_NAME = "IA_CLASS_NAME";
    private static final String PLACEHOLDER_IA_ARTIFACT_TEMPLATE_UPLOAD_URL = "IA_ARTIFACT_TEMPLATE_UPLOAD_URL";

    // Placeholders in Java Service Files
    private static final String PLACEHOLDER_GENERATED_WEBSERVICE_METHODS = "GENERATED_WEBSERVICE_METHODS";

    // Template folder relative to resources folder in this project
    private static final String TEMPLATE_PROJECT_FOLDER = "template/project";
    private static final String TEMPLATE_JAVA_FOLDER = "template/java";

    private static final String TEMPLATE_JAVA_ABSTRACT_IA_SERVICE = "AbstractIAService.java.template";
    private static final String TEMPLATE_JAVA_TEMPLATE_SERVICE = "TemplateService.java.template";

    private final TInterface tInterface;
    private final File workingDir;
    private final File outDir;
    private final String name;
    private final String javaPackage;
    private final String namespace;
    private final URL iaArtifactTemplateUploadUrl;

    /**
     * Creates a new IA Generator instance for the given {@link TInterface}.
     *
     * @param tInterface                  TOSCA interface to generate the IA for
     * @param packageAndNamespace         Package to be used for the generated Java code, e.g. 'org.opentosca.ia'. To
     *                                    generate the respective Namespace for the Web Service the components of the
     *                                    package are reverted, prepended with 'http://' and appended with '/'. This is
     *                                    provided by the user in a textfield in the Winery UI.
     * @param iaArtifactTemplateUploadUrl The URL to which the generated IA should be posted.
     * @param name                        unique and valid name to be used for the generated maven project name, java
     *                                    project name, class name, port type name.
     * @param workingDir                  working directory to generate the files. This directory also will contain the
     *                                    ZIP file with the Eclipse project after generating it.
     */
    public Generator(TInterface tInterface, String packageAndNamespace, URL iaArtifactTemplateUploadUrl, String name, File workingDir) {
        super();
        this.tInterface = tInterface;
        this.javaPackage = packageAndNamespace;
        this.iaArtifactTemplateUploadUrl = iaArtifactTemplateUploadUrl;
        this.name = name;
        this.workingDir = new File(workingDir.getAbsolutePath() + File.separator + this.name);
        this.outDir = new File(workingDir.getAbsolutePath());

        if (this.workingDir.exists()) {
            Generator.LOGGER.error("Workdir " + this.workingDir + " already exits. This might lead to corrupted results if it is not empty!");
        }

        // Generate Namespace
        String[] splitPkg = this.javaPackage.split("\\.");
        String tmpNamespace = "http://";
        for (int i = splitPkg.length - 1; i >= 0; i--) {
            tmpNamespace += splitPkg[i];
            // Add '.' if it is not the last iterations
            if (i != 0) {
                tmpNamespace += ".";
            }
        }
        this.namespace = tmpNamespace += "/";
    }

    /**
     * Generates the project and puts the files into a subdirectory of working dir
     *
     * @return the folder where the generated files are contained in
     */
    public Path generateProject() throws Exception {
        Path workingDirPath = this.workingDir.toPath();
        Files.createDirectories(workingDirPath);

        // directory to store the template files to generate the java files from
        Path javaTemplateDir = workingDirPath.resolve("../java");
        Files.createDirectories(javaTemplateDir);

        // Copy template project and template java files
        String s = this.getClass().getResource("").getPath();
        if (s.contains("jar!")) {
            Generator.LOGGER.trace("we work on a jar file");
            Generator.LOGGER.trace("Location of the current class: {}", s);

            // we have a jar file
            // format: file:/location...jar!...path-in-the-jar
            // we only want to have location :)
            int excl = s.lastIndexOf("!");
            s = s.substring(0, excl);
            s = s.substring("file:".length());

            try (JarFile jf = new JarFile(s);) {
                Enumeration<JarEntry> entries = jf.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = entries.nextElement();
                    String name = je.getName();
                    if (name.startsWith(Generator.TEMPLATE_PROJECT_FOLDER + "/") && (name.length() > (Generator.TEMPLATE_PROJECT_FOLDER.length() + 1))) {
                        // strip "template/" from the beginning to have paths without "template" starting relatively from the working dir
                        name = name.substring(Generator.TEMPLATE_PROJECT_FOLDER.length() + 1);
                        if (je.isDirectory()) {
                            // directory found
                            Path dir = workingDirPath.resolve(name);
                            Files.createDirectory(dir);
                        } else {
                            Path file = workingDirPath.resolve(name);
                            try (InputStream is = jf.getInputStream(je);) {
                                Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    } else if (name.startsWith(Generator.TEMPLATE_JAVA_FOLDER + "/") && (name.length() > (Generator.TEMPLATE_JAVA_FOLDER.length() + 1))) {
                        if (!je.isDirectory()) {
                            // we copy the file directly into javaTemplateDir
                            File f = new File(name);
                            Path file = javaTemplateDir.resolve(f.getName());
                            try (InputStream is = jf.getInputStream(je);) {
                                Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            }
        } else {
            // we're running in debug mode, we can work on the plain file system
            File templateProjectDir = new File(this.getClass().getResource("/" + Generator.TEMPLATE_PROJECT_FOLDER).getFile());
            FileUtils.copyDirectory(templateProjectDir, this.workingDir);

            File javaTemplatesDir = new File(this.getClass().getResource("/" + Generator.TEMPLATE_JAVA_FOLDER).getFile());
            FileUtils.copyDirectory(javaTemplatesDir, javaTemplateDir.toFile());
        }

        // Create Java Code Folder
        String[] splitPkg = this.javaPackage.split("\\.");
        StringBuilder javaFolderString = new StringBuilder(this.workingDir.getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "java");
        for (String value : splitPkg) {
            javaFolderString.append(File.separator).append(value);
        }

        // Copy and rename TEMPLATE_JAVA_TEMPLATE_SERVICE
        Path templateJavaService = javaTemplateDir.resolve(Generator.TEMPLATE_JAVA_TEMPLATE_SERVICE);
        File javaService = new File(javaFolderString + File.separator + this.name + ".java");
        Files.createDirectories(javaService.toPath().getParent());
        Files.copy(templateJavaService, javaService.toPath(), StandardCopyOption.REPLACE_EXISTING);

        this.generateJavaFile(javaService);
        this.updateFilesRecursively(this.workingDir);

        return this.workingDir.toPath();
    }

    /**
     * Generates the IA project into the working dir and zipps it
     *
     * @return The ZIP file containing the maven/eclipse project to be downloaded by the user.
     */
    public File generateAndZipProject() throws Exception {
        final Path path = this.generateProject();
        return this.packageProject(path);
    }

    private void generateJavaFile(File javaService) throws IOException {

        // Generate methods
        StringBuilder sb = new StringBuilder();

        for (TOperation op : this.tInterface.getOperation()) {
            // Annotations
            sb.append("\t@WebMethod\n");
            sb.append("\t@SOAPBinding\n");
            sb.append("\t@Oneway\n");

            // Signatur
            String operationReturn = "void";
            sb.append("\tpublic ").append(operationReturn).append(" ").append(op.getName()).append("(\n");

            // Parameter
            boolean first = true;
            if (op.getInputParameters() != null) {
                for (TParameter parameter : op.getInputParameters().getInputParameter()) {
                    String parameterName = parameter.getName();

                    if (first) {
                        first = false;
                        sb.append("\t\t");
                    } else {
                        sb.append(",\n\t\t");
                    }

                    // Generate @WebParam
                    sb.append("@WebParam(name=\"").append(parameterName).append("\", targetNamespace=\"").append(this.namespace).append("\") ");

                    sb.append("String ").append(parameterName);
                }
            }
            sb.append("\n\t) {\n");

            // If there are output parameters we generate the respective HashMap
            boolean outputParamsExist = (op.getOutputParameters() != null) && (!op.getOutputParameters().getOutputParameter().isEmpty());
            if (outputParamsExist) {
                sb.append("\t\t// This HashMap holds the return parameters of this operation.\n");
                sb.append("\t\tfinal HashMap<String,String> returnParameters = new HashMap<String, String>();\n\n");
            }

            sb.append("\t\t// TODO: Implement your operation here.\n");

            // Generate code to set output parameters
            if (outputParamsExist) {
                for (TParameter outputParam : op.getOutputParameters().getOutputParameter()) {
                    sb.append("\n\n\t\t// Output Parameter '").append(outputParam.getName()).append("' ");
                    if (outputParam.getRequired()) {
                        sb.append("(required)");
                    } else {
                        sb.append("(optional)");
                    }
                    sb.append("\n\t\t// TODO: Set ").append(outputParam.getName()).append(" parameter here.");
                    sb.append("\n\t\t// Do NOT delete the next line of code. Set \"\" as value if you want to return nothing or an empty result!");
                    sb.append("\n\t\treturnParameters.put(\"").append(outputParam.getName()).append("\", \"TODO\");");
                }
                sb.append("\n\n\t\tsendResponse(returnParameters);\n");
            }

            sb.append("\t}\n\n");
        }

        // Read file and replace placeholders
        Charset cs = Charset.defaultCharset();
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(javaService.toPath(), cs)) {
            // Replace web service method
            line = line.replaceAll(Generator.PLACEHOLDER_GENERATED_WEBSERVICE_METHODS, sb.toString());
            lines.add(line);
        }

        // Write file
        OpenOption[] options = new OpenOption[] {StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};
        Files.write(javaService.toPath(), lines, cs, options);
    }

    /**
     * Iterates recursively through all the files in the project working directory and tries to replace the global
     * placeholders.
     *
     * @param folderOrFile to start with
     */
    private void updateFilesRecursively(File folderOrFile) {
        if (folderOrFile.isFile()) {

            if (folderOrFile.getAbsolutePath().endsWith(".jar")) {
                return;
            }

            Generator.LOGGER.trace("Updating file " + folderOrFile);

            try {
                // Read file and replace placeholders
                Charset cs = Charset.defaultCharset();
                List<String> lines = new ArrayList<>();
                for (String line : Files.readAllLines(folderOrFile.toPath(), cs)) {
                    line = line.replaceAll(Generator.PLACEHOLDER_CLASS_NAME, this.name);
                    line = line.replaceAll(Generator.PLACEHOLDER_JAVA_PACKAGE, this.javaPackage);
                    line = line.replaceAll(Generator.PLACEHOLDER_NAMESPACE, this.namespace);
                    line = line.replaceAll(Generator.PLACEHOLDER_IA_ARTIFACT_TEMPLATE_UPLOAD_URL, this.iaArtifactTemplateUploadUrl.toString());
                    lines.add(line);
                }

                // Write file
                OpenOption[] options = new OpenOption[] {StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};
                Files.write(folderOrFile.toPath(), lines, cs, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Generator.LOGGER.trace("Updating folder " + folderOrFile);
            for (File childFile : folderOrFile.listFiles()) {
                this.updateFilesRecursively(childFile);
            }
        }
    }

    /**
     * Packages the generated project into a ZIP file which is stored in outDir and has the name of the Project.
     *
     * @param projectDir the dir where the generated files reside in
     * @return ZIP file
     */
    private File packageProject(Path projectDir) {
        try {
            File packagedProject = new File(this.outDir.getAbsoluteFile() + File.separator + this.name + ".zip");
            FileOutputStream fileOutputStream = new FileOutputStream(packagedProject);
            final ArchiveOutputStream zos = new ArchiveStreamFactory().createArchiveOutputStream("zip", fileOutputStream);

            this.addFilesRecursively(projectDir.toAbsolutePath().toFile(), projectDir.toAbsolutePath() + File.separator, zos);

            zos.finish();
            zos.close();

            return packagedProject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recursive Helper function for packageProject()
     *
     * @param folderOrFile to add into the archive
     * @param baseDir      the base directory, which is to be stripped from the file name
     * @param zos          ArchiveOutputStream to add the files to
     */
    private void addFilesRecursively(File folderOrFile, String baseDir, ArchiveOutputStream zos) {
        if (folderOrFile.isFile()) {
            String nameOfFileInZip = folderOrFile.getAbsolutePath().replace(baseDir, "");
            Generator.LOGGER.trace("Adding " + folderOrFile + " as " + nameOfFileInZip);
            ArchiveEntry archiveEntry = new ZipArchiveEntry(nameOfFileInZip);
            try (InputStream is = new FileInputStream(folderOrFile)) {
                zos.putArchiveEntry(archiveEntry);
                IOUtils.copy(is, zos);
                zos.closeArchiveEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Generator.LOGGER.trace("Adding folder " + folderOrFile);
            for (File childFile : folderOrFile.listFiles()) {
                this.addFilesRecursively(childFile, baseDir, zos);
            }
        }
    }
}
