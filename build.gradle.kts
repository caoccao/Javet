plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
    jcenter()
}

group = "com.caoccao.javet"
version = "0.7.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    runtimeOnly(fileTree("libs"))
}

task<Exec>("buildJNIHeaders") {
    project.exec {
        workingDir("$projectDir")
        commandLine(
            "javac",
            "-h",
            "cpp/jni",
            "-d",
            "build/generated/tmp/jni",
            "src/main/java/com/caoccao/javet/interop/V8Native.java"
        )
    }
}

tasks.test {
    useJUnitPlatform()
}
