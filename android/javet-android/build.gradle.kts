/*
 *    Copyright 2021-2023. caoccao.com Sam Cao
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

import org.apache.tools.ant.taskdefs.condition.Os

object Config {
    const val GROUP_ID = "com.caoccao.javet"
    const val NAME = "Javet Android"
    const val VERSION = Versions.JAVET
    const val URL = "https://github.com/caoccao/Javet"

    object Pom {
        const val ARTIFACT_ID = "javet-android"
        const val DESCRIPTION =
            "Javet is Java + V8 (JAVa + V + EighT). It is an awesome way of embedding Node.js and V8 in Java."

        object Developer {
            const val ID = "caoccao"
            const val EMAIL = "sjtucaocao@gmail.com"
            const val NAME = "Sam Cao"
            const val ORGANIZATION = "caoccao.com"
            const val ORGANIZATION_URL = "https://www.caoccao.com"
        }

        object License {
            const val NAME = "APACHE LICENSE, VERSION 2.0"
            const val URL = "https://github.com/caoccao/Javet/blob/main/LICENSE"
        }

        object Scm {
            const val CONNECTION = "scm:git:git://github.com/Javet.git"
            const val DEVELOPER_CONNECTION = "scm:git:ssh://github.com/Javet.git"
        }
    }

    object Projects {
        // https://androidx.tech/artifacts/test.ext/junit
        const val ANDROIDX_ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ANDROIDX_ESPRESSO_CORE}"

        // https://androidx.tech/artifacts/test.espresso/espresso-core/
        const val ANDROIDX_TEST = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST}"

        // https://mvnrepository.com/artifact/androidx.appcompat/appcompat
        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"

        // https://developer.android.com/studio/write/java8-support
        // https://mvnrepository.com/artifact/com.android.tools/desugar_jdk_libs
        const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:${Versions.DESUGAR_JDK_LIBS}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        const val JUNIT_JUPITER_API = "org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT}"

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
        const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}"
    }

    object Versions {
        const val ANDROIDX_ESPRESSO_CORE = "3.5.1"
        const val ANDROIDX_TEST = "1.1.5"
        const val APPCOMPAT = "1.3.1"
        const val DESUGAR_JDK_LIBS = "2.0.4"
        const val JAVET = "3.0.4"
        const val JUNIT = "5.10.1"
    }
}

plugins {
    id("com.android.library")
}

group = Config.GROUP_ID
version = Config.VERSION

android {
    compileSdk = 30
    namespace = Config.GROUP_ID

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint.abortOnError = false

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

dependencies {
    coreLibraryDesugaring(Config.Projects.DESUGAR_JDK_LIBS)
    implementation(Config.Projects.APPCOMPAT)
    testImplementation(Config.Projects.JUNIT_JUPITER_API)
    testRuntimeOnly(Config.Projects.JUNIT_JUPITER_ENGINE)
    androidTestImplementation(Config.Projects.ANDROIDX_ESPRESSO_CORE)
    androidTestImplementation(Config.Projects.ANDROIDX_TEST)
}

tasks.register<Jar>(name = "sourceJar") {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

task<Exec>("syncSourceCode") {
    project.exec {
        workingDir("$projectDir/../../scripts/python")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            commandLine("cmd", "/c", "python", "patch_android_build.py")
        } else {
            commandLine("sh", "-c", "python3", "patch_android_build.py")
        }
    }
}
