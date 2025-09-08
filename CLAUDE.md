# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Javet (Java + V8) is a Java library that embeds Node.js and V8 JavaScript engine in Java applications. It provides seamless JavaScript and Java interoperability with support for both Node.js and standalone V8 modes.

## Architecture

### Core Components

- **V8Host**: Main entry point for creating V8/Node.js runtimes. Handles runtime lifecycle management and native library loading via `JavetLibLoader`.
- **V8Runtime/NodeRuntime**: The primary runtime interfaces. `NodeRuntime` extends `V8Runtime` with Node.js-specific functionality like `require()` support.
- **Interop Layer**: Located in `src/main/java/com/caoccao/javet/interop/`, handles Java-JavaScript value conversion and binding.
- **Native Layer**: C++ JNI code in `cpp/jni/` directory bridges Java and the underlying V8/Node.js engines.

### Key Packages

- `com.caoccao.javet.annotations`: Annotations for V8 binding (@V8Function, @V8Property, etc.)
- `com.caoccao.javet.values`: V8 value types (V8ValueObject, V8ValueFunction, etc.)
- `com.caoccao.javet.interception`: Interceptors for debugging and logging
- `com.caoccao.javet.exceptions`: Javet-specific exception hierarchy
- `com.caoccao.javet.node.modules`: Node.js module implementations

### Multi-Platform Support

The project supports multiple architectures via separate native library artifacts:
- Linux: x86_64, arm64  
- macOS: x86_64, arm64
- Windows: x86_64
- Android: x86, x86_64, arm, arm64

## Common Development Commands

### Building
```bash
./gradlew build              # Build entire project
./gradlew jar               # Build main JAR
./gradlew javadocJar        # Build Javadoc JAR
./gradlew sourcesJar        # Build sources JAR
```

### Testing
```bash
./gradlew test              # Run standard tests (excludes performance tests)
./gradlew performanceTest   # Run performance tests only
./gradlew check             # Run all verification tasks
```

### Native Development
```bash
./gradlew buildJNIHeaders   # Generate JNI headers for C++ development
```

### Build System Details

- **Build Tool**: Gradle with Kotlin DSL
- **Java Version**: Java 8 (for maximum compatibility)
- **Test Framework**: JUnit 5
- **Native Build**: CMake for C++ components

## Development Workflow

### Java Development
1. Main source code is in `src/main/java/com/caoccao/javet/`
2. Test code follows standard Maven structure in `src/test/java/`
3. Use existing patterns for V8 value handling and exception management
4. Follow the annotation-based binding system for Java-JavaScript interop

### Native Development  
1. JNI headers are auto-generated via `buildJNIHeaders` task
2. C++ source is in `cpp/jni/` directory
3. Native libraries are platform-specific and loaded dynamically

### Testing Strategy
- Standard unit tests exclude performance tests by default
- Performance tests are tagged and run separately
- Tests require platform-specific native libraries to be available

## Important Notes

- The project uses JNI extensively - be careful with native resource management
- V8 contexts and values must be properly closed to prevent memory leaks
- Runtime creation is expensive - use pooling for production scenarios
- Different runtime types (V8 vs Node.js) have different capabilities and APIs
- Platform-specific native dependencies are required for running tests and applications