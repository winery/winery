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

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.eclipse.winery.lsp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val recordBuilderVersion = "42"

dependencies {
    implementation("org.tinylog:tinylog-api:2.7.0")
    implementation("org.tinylog:tinylog-impl:2.7.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.12.0")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j.jsonrpc:0.12.0")
    implementation("org.yaml:snakeyaml:2.0")
    compileOnly("io.soabase.record-builder:record-builder-core:$recordBuilderVersion")
    annotationProcessor("io.soabase.record-builder:record-builder-processor:$recordBuilderVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.eclipse.winery.lsp.Launcher.StdioLauncher"
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
