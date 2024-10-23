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
