# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Javet (Java + V8) is a Java library that embeds Node.js and V8 JavaScript engine in Java applications. It provides seamless JavaScript and Java interoperability with support for both Node.js and standalone V8 modes.

## Architecture

### Core Components

- **V8Host**: Main entry point for creating V8/Node.js runtimes. Handles runtime lifecycle management and native library loading via `JavetLibLoader`. Manages a daemon for runtime guards and statistics futures.
- **V8Runtime/NodeRuntime**: The primary runtime interfaces. `NodeRuntime` extends `V8Runtime` with Node.js-specific functionality like `require()` support. Represents a V8 isolate with a single context (Javet simplifies V8's multi-context model to 1 runtime = 1 isolate = 1 context).
- **Interop Layer**: Located in `src/main/java/com/caoccao/javet/interop/`, handles Java-JavaScript value conversion and binding.
- **Native Layer**: C++ JNI code in `cpp/jni/` directory bridges Java and the underlying V8/Node.js engines.
- **Engine Pool**: `JavetEnginePool` in `interop/engine/` provides pooling for expensive runtime creation. Use in production for performance.
- **Converters**: Located in `interop/converters/`, handle type conversion between Java and JavaScript (`JavetObjectConverter`, `JavetPrimitiveConverter`, `JavetProxyConverter`, `JavetBridgeConverter`).

### Key Packages

- `com.caoccao.javet.annotations`: Annotations for V8 binding (@V8Function, @V8Property, @V8Getter, @V8Setter, etc.)
- `com.caoccao.javet.values`: V8 value types (V8ValueObject, V8ValueFunction, etc.)
- `com.caoccao.javet.interception`: Interceptors for debugging and logging (`JavetStandardConsoleInterceptor`, `JavetJVMInterceptor`)
- `com.caoccao.javet.exceptions`: Javet-specific exception hierarchy
- `com.caoccao.javet.node.modules`: Node.js module implementations
- `com.caoccao.javet.interop.binding`: Binding system for proxying Java objects to JavaScript
- `com.caoccao.javet.interop.executors`: Executors for running scripts (V8StringExecutor, V8FileExecutor, V8PathExecutor)

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
./gradlew jar                # Build main JAR
./gradlew javadocJar         # Build Javadoc JAR
./gradlew sourcesJar         # Build sources JAR
./gradlew clean              # Clean build directory
```

### Testing
```bash
./gradlew test              # Run standard tests (excludes performance tests)
./gradlew performanceTest   # Run performance tests only (tagged with @Tag("performance"))
./gradlew check             # Run all verification tasks
```

To run a single test class:
```bash
./gradlew test --tests "com.caoccao.javet.exceptions.TestJavetError"
```

To run a single test method:
```bash
./gradlew test --tests "com.caoccao.javet.exceptions.TestJavetError.testErrorType"
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
5. All tests extend `BaseTestJavet` or `BaseTestJavetRuntime` which sets up V8 flags and provides common test utilities

### Native Development
1. JNI headers are auto-generated via `buildJNIHeaders` task
2. C++ source is in `cpp/jni/` directory
3. Native libraries are platform-specific and loaded dynamically
4. The native interfaces are `IV8Native` (implemented by `V8Native`) and `INodeNative` (implemented by `NodeNative`)

### Testing Strategy
- Standard unit tests exclude performance tests by default
- Performance tests are tagged with `@Tag("performance")` and run separately via `performanceTest` task
- Tests require platform-specific native libraries to be available
- Base test classes provide setup: `BaseTestJavet`, `BaseTestJavetPool`, `BaseTestJavetRuntime`

## Important Notes

### Resource Management
- The project uses JNI extensively - be careful with native resource management
- V8 values and runtimes implement `IJavetClosable` and must be properly closed to prevent memory leaks
- Use try-with-resources pattern for V8 values and runtimes
- Runtime creation is expensive - use `JavetEnginePool` for production scenarios

### Runtime Types
- **V8 Mode**: Standalone V8 engine without Node.js APIs
- **Node Mode**: Full Node.js with `require()`, built-in modules, and V8 engine
- Different runtime types have different capabilities - choose based on your needs
- Switch between modes using `JSRuntimeType.V8` or `JSRuntimeType.Node` when creating runtimes

### Type Conversion
- Javet provides multiple converter strategies: `JavetObjectConverter` (default), `JavetPrimitiveConverter`, `JavetProxyConverter`, `JavetBridgeConverter`
- Converters handle automatic type mapping between Java and JavaScript
- Custom converters can be implemented via `IJavetConverter`

### Platform Dependencies
- Platform-specific native dependencies are required for running tests and applications
- Native libraries must match the OS and architecture (x86_64, arm64, etc.)
- Libraries are loaded via `JavetLibLoader` at runtime