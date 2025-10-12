# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

V8 is Google's open source JavaScript and WebAssembly engine, written in C++. It implements ECMAScript and is used in Chrome and Node.js. This is a large, performance-critical codebase where correctness bugs often lead to security issues.

## Build System

V8 uses GN (Generate Ninja) as its meta-build system. The primary build wrapper is `tools/dev/gm.py`.

### Common Build Commands

```bash
# Build d8 shell (x64, optimized debug) - recommended for development
tools/dev/gm.py quiet x64.optdebug

# Build d8 shell (x64, debug) - slower, full debug info
tools/dev/gm.py quiet x64.debug

# Build d8 shell (x64, release) - for benchmarking
tools/dev/gm.py quiet x64.release

# Build specific target
tools/dev/gm.py quiet x64.optdebug cctest

# Clean and rebuild
tools/dev/gm.py x64.optdebug.clean
```

**Important**: Always use the `quiet` keyword unless explicitly asked otherwise, to avoid wasting tokens on compilation output. Errors will still be reported.

**Build Modes**:
- `release`: Optimized, no debug info. Use for benchmarking only.
- `debug`: Full debug info, assertions enabled. Slow but essential for debugging.
- `optdebug`: Optimizations + debug info. Best for general development.

**Build Outputs**: Binaries are placed in `out/<arch>.<mode>/` (e.g., `out/x64.optdebug/d8`).

### Alternative: v8gen.py

For custom configurations:
```bash
# Generate build with custom args
tools/dev/v8gen.py x64.release -- v8_enable_slow_dchecks=true
```

## Testing

### Running Tests

The primary test runner is `tools/run-tests.py`:

```bash
# Run all standard tests for a build
tools/run-tests.py --progress dots --exit-after-n-failures=5 --outdir=out/x64.optdebug

# Run specific test suite
tools/run-tests.py --progress dots --exit-after-n-failures=5 --outdir=out/x64.optdebug cctest

# Run specific test file
tools/run-tests.py --progress dots --exit-after-n-failures=5 --outdir=out/x64.optdebug cctest/test-heap

# Run C++ tests only
tools/run-tests.py --progress dots --exit-after-n-failures=5 --outdir=out/x64.optdebug cctest unittests

# Run JavaScript tests
tools/run-tests.py --progress dots --exit-after-n-failures=5 --outdir=out/x64.optdebug mjsunit
```

**Important**: Always use `--progress dots` to minimize output and avoid token waste.

### Test Suites

- **unittests**: Modern C++ unit tests (preferred)
- **cctest**: Older C++ unit tests (being migrated to unittests)
- **mjsunit**: JavaScript language feature tests
- **test262**: Official ECMAScript conformance tests
- **wasm-spec-tests**: WebAssembly specification tests
- **inspector**: DevTools protocol tests
- **message**: Tests for error messages

### Reproducing Test Failures

When a test fails, the output includes a command to reproduce it:
```bash
Command: out/x64.optdebug/d8 --test test/mjsunit/mjsunit.js test/mjsunit/foo.js --random-seed=-190258694
```

You can run this directly or use:
```bash
tools/run-tests.py --progress dots --outdir=out/x64.optdebug mjsunit/foo
```

## Code Structure

### Source Layout (`src/`)

Key directories (see full list in GEMINI.md):
- **`src/api/`**: Public C++ API implementation (headers in `include/`)
- **`src/builtins/`**: JavaScript built-in functions (Array.map, etc.)
- **`src/compiler/`**: TurboFan optimizing compiler + Turboshaft
- **`src/interpreter/`**: Ignition bytecode interpreter
- **`src/baseline/`**: Sparkplug baseline compiler
- **`src/maglev/`**: Maglev mid-tier optimizing compiler
- **`src/heap/`**: Garbage collector and memory management
- **`src/objects/`**: V8 internal and JavaScript object representations
- **`src/codegen/`**: Machine code generation (architecture-specific in subdirs)
- **`src/wasm/`**: WebAssembly implementation
- **`src/execution/`**: Isolates, frames, microtasks, tiering
- **`src/sandbox/`**: V8 sandbox security feature
- **`src/torque/`**: Torque language compiler

### Public API (`include/`)

All public V8 APIs are in `include/`. Main header is `include/v8.h`. These headers are used when embedding V8 (e.g., in Chrome, Node.js).

### Tests (`test/`)

Test suites match their names: `test/cctest/`, `test/unittests/`, `test/mjsunit/`, etc.

## Torque Language

Torque is V8's domain-specific language for writing builtins and object definitions. It compiles to CodeStubAssembler (CSA) code.

### Key Concepts

- **File Extension**: `.tq` (in `src/builtins/` and `src/objects/`)
- **Compilation**: Automatic during build. Generated files go to `out/<build>/gen/torque-generated/`
- **Syntax**: TypeScript-like with V8-specific extensions

### Torque Keywords

- `macro`: Inlined function for reusable logic
- `builtin`: Non-inlined function, callable from JS or other builtins
- `javascript`: Marks a builtin as directly callable from JavaScript
- `transitioning`: Function can cause object map changes
- `extern`: Declares C++ CSA function callable from Torque

### Generated Files

Torque generates multiple file types in `out/<build>/gen/torque-generated/`:
- `*-tq.cc`, `*-tq.inc`, `*-tq-inl.inc`: Implementation files
- `builtin-definitions.h`: List of all builtins
- `class-forward-declarations.h`: Forward declarations
- `factory.cc`: Factory functions for Torque classes
- `instance-types.h`: InstanceType enum

### Debugging Torque

When debugging Torque issues, inspect the generated C++ in `out/<build>/gen/torque-generated/` to see the actual CSA code being executed.

### Workflow

1. Modify `.tq` file in `src/builtins/` or `src/objects/`
2. Rebuild: `tools/dev/gm.py quiet x64.optdebug`
3. Run tests to verify changes

## Debugging

### Running d8 with Debuggers

```bash
# Run d8 with GDB (Linux/Mac)
gdb --args out/x64.debug/d8 --my-flag my-script.js

# Run d8 with LLDB (Mac)
lldb -- out/x64.debug/d8 --my-flag my-script.js
```

### Common V8 Flags

- `--trace-opt`: Log optimized functions
- `--trace-deopt`: Log deoptimizations with reasons
- `--trace-gc`: Log GC events
- `--allow-natives-syntax`: Enable internal V8 functions like `%OptimizeFunctionOnNextCall(f)`
- `--print-bytecode`: Print Ignition bytecode
- `--print-opt-code`: Print optimized code
- `--code-comments`: Include comments in generated code

View all flags: `out/x64.debug/d8 --help`

Most flags are in `src/flags/flag-definitions.h`. d8-specific flags are in `src/d8/d8.cc` (`Shell::SetOptions`).

## Code Style and Formatting

### Formatting

```bash
# Format all changes before committing
git cl format
```

### Style Guide

Follow [Chromium's C++ style guide](https://chromium.googlesource.com/chromium/src/+/main/styleguide/styleguide.md).

### Commit Message Format

```
[component]: Short description

Longer explanation of why the change is needed, not just what changed.
Wrap at 72 characters.

Bug: 123456
```

Component examples: `compiler`, `runtime`, `api`, `heap`, `wasm`, `builtins`, `torque`

Bug tracker: https://crbug.com/

## Important Development Practices

### Architecture-Specific Code Synchronization

When modifying architecture-specific code in `src/codegen/`, `src/regexp/`, or other arch-specific directories, changes must be kept in sync across all architectures:
- x64, ia32, arm, arm64, mips64, ppc64, riscv32, riscv64, s390x, loong64

### Don't Edit Generated Files

Never edit files in `out/` or generated files. Always modify the source:
- For Torque: Edit `.tq` files
- For protocol definitions: Edit `.pdl` files
- Generated files are rebuilt automatically

### Header Inclusion

- Many functions are declared in `.h` files but defined in `-inl.h` files
- Only include `-inl.h` files from other `-inl.h` files or `.cc` files
- If you get missing definition errors, you likely need to include a `-inl.h`

### Forward Declarations

Many types are forward-declared. To use them, find and include the definition header. Don't guess header names; search for them.

## Performance and Security Mindset

- **Performance**: V8 is performance-critical. When optimizing, always benchmark changes.
- **Security**: V8 runs untrusted code. Correctness bugs typically become security vulnerabilities. Code must be bug-free and handle all edge cases.
- **Testing**: Thorough testing is mandatory. Always run relevant test suites after changes.

## Dependency Management

Dependencies are managed via `DEPS` file and `gclient`:

```bash
# Update V8 and dependencies
git pull origin
gclient sync
```

## Build Artifacts

- `out/<arch>.<mode>/d8`: The d8 shell (REPL and script runner)
- `out/<arch>.<mode>/cctest`: C++ test runner
- `out/<arch>.<mode>/v8_unittests`: Unit test runner
- `out/<arch>.<mode>/mksnapshot`: Snapshot generator
- `out/<arch>.<mode>/gen/`: Generated source files

## Quick Reference: Common Tasks

### Single Test Workflow
```bash
# Build + run single test suite
tools/dev/gm.py quiet x64.optdebug cctest

# Or combined: build and run tests
tools/dev/gm.py quiet x64.optdebug.check
```

### Full Build and Test
```bash
# Build all and run standard tests
tools/dev/gm.py quiet x64.optdebug.check
```

### Adding a New Builtin (Torque)
1. Create or modify `.tq` file in `src/builtins/`
2. Build: `tools/dev/gm.py quiet x64.optdebug`
3. Test: `tools/run-tests.py --progress dots --outdir=out/x64.optdebug mjsunit`
4. Format: `git cl format`

### Debugging a Crash
1. Build debug: `tools/dev/gm.py quiet x64.debug`
2. Reproduce in GDB: `gdb --args out/x64.debug/d8 test.js`
3. Add flags like `--trace-opt` or `--trace-gc` to narrow down issue

## File Naming Patterns

- `.h`: Header declarations
- `.cc`: Implementation files
- `-inl.h`: Inline function definitions (include only from `.cc` or other `-inl.h`)
- `.tq`: Torque source files
- `-tq.cc`, `-tq.inc`: Generated from Torque
- `.pdl`: Protocol definition files (for inspector)
