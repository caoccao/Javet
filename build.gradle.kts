/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object Config {
    const val GROUP_ID = "com.caoccao.javet"
    const val NAME = "Javet"
    const val VERSION = Versions.JAVET
    const val URL = "https://github.com/caoccao/JavetShell"

    object Projects {
        // https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy
        const val BYTE_BUDDY = "net.bytebuddy:byte-buddy:${Versions.BYTE_BUDDY}"

        // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
        const val JACKSON_DATABIND = "com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON_DATABIND}"

        // https://mvnrepository.com/artifact/org.eclipse.jetty.websocket/javax-websocket-server-impl
        const val JETTY_JAVAX_WEBSOCKET_SERVER_IMPL = "org.eclipse.jetty.websocket:javax-websocket-server-impl:${Versions.JETTY_WEBSOCKET}"

        // https://mvnrepository.com/artifact/org.eclipse.jetty.websocket/websocket-server
        const val JETTY_WEBSOCKET_SERVER = "org.eclipse.jetty.websocket:websocket-server:${Versions.JETTY_WEBSOCKET}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        const val JUNIT_JUPITER_API = "org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
        const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params
        const val JUNIT_JUPITER_PARAMS = "org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT}"
    }

    object Versions {
        const val BYTE_BUDDY = "1.14.10"
        const val JACKSON_DATABIND = "2.16.0"
        const val JAVET = "3.0.3"
        const val JETTY_WEBSOCKET = "9.4.53.v20231009"
        const val JUNIT = "5.10.1"
    }
}

plugins {
    java
    `java-library`
    `maven-publish`
}

group = Config.GROUP_ID
version = Config.VERSION

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(Config.Projects.BYTE_BUDDY)
    testImplementation(Config.Projects.JACKSON_DATABIND)
    testImplementation(Config.Projects.JETTY_JAVAX_WEBSOCKET_SERVER_IMPL)
    testImplementation(Config.Projects.JETTY_WEBSOCKET_SERVER)
    testImplementation(Config.Projects.JUNIT_JUPITER_API)
    testImplementation(Config.Projects.JUNIT_JUPITER_PARAMS)
    testRuntimeOnly(Config.Projects.JUNIT_JUPITER_ENGINE)
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

tasks.jar {
    manifest {
        attributes["Automatic-Module-Name"] = Config.GROUP_ID
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

tasks.withType<Javadoc> {
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
