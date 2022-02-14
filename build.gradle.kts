/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

plugins {
    java
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

group = "com.caoccao.javet"
version = "1.1.0"

dependencies {
    testImplementation("org.eclipse.jetty.websocket:websocket-server:9.4.44.v20210927")
    testImplementation("org.eclipse.jetty.websocket:javax-websocket-server-impl:9.4.44.v20210927")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

afterEvaluate {
    tasks.withType(JavaCompile::class) {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

task<Exec>("buildJNIHeaders") {
    mkdir("$buildDir/generated/tmp/jni")
    project.exec {
        workingDir("$projectDir")
        commandLine(
            "javac",
            "-h",
            "cpp/jni",
            "-d",
            "$buildDir/generated/tmp/jni",
            "src/main/java/com/caoccao/javet/interop/INodeNative.java",
            "src/main/java/com/caoccao/javet/interop/IV8Native.java",
            "src/main/java/com/caoccao/javet/interop/NodeNative.java",
            "src/main/java/com/caoccao/javet/interop/V8Native.java"
        )
    }
}

tasks.test {
    useJUnitPlatform {
        excludeTags("performance")
    }
}

tasks.register<Test>("performanceTest") {
    useJUnitPlatform {
        includeTags("performance")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}

// Allow for publishing to maven local
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
