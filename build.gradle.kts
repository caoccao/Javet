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
}

repositories {
    mavenCentral()
    jcenter()
}

group = "com.caoccao.javet"
version = "0.7.4"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("javax.websocket:javax.websocket-api:1.1")
    testImplementation("org.eclipse.jetty.websocket:javax-websocket-server-impl:9.4.38.v20210224")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    runtimeOnly(fileTree("libs"))
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
