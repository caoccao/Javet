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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.enums.JavetErrorType;
import com.caoccao.javet.utils.SimpleFreeMarkerFormat;

import java.util.Map;

/**
 * The type Javet error.
 *
 * @since 0.8.5
 */
public class JavetError {
    /**
     * The constant PARAMETER_ACTUAL_PARAMETER_SIZE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_ACTUAL_PARAMETER_SIZE = "actualParameterSize";
    /**
     * The constant PARAMETER_ACTUAL_PARAMETER_TYPE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_ACTUAL_PARAMETER_TYPE = "actualParameterType";
    /**
     * The constant PARAMETER_CALLBACK_TYPE.
     *
     * @since 2.2.0
     */
    public static final String PARAMETER_CALLBACK_TYPE = "callbackType";
    /**
     * The constant PARAMETER_CONTINUABLE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_CONTINUABLE = "continuable";
    /**
     * The constant PARAMETER_COUNT.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_COUNT = "count";
    /**
     * The constant PARAMETER_CURRENT_THREAD_ID.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_CURRENT_THREAD_ID = "currentThreadId";
    /**
     * The constant PARAMETER_END_COLUMN.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_END_COLUMN = "endColumn";
    /**
     * The constant PARAMETER_END_POSITION.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_END_POSITION = "endPosition";
    /**
     * The constant PARAMETER_EXPECTED_PARAMETER_SIZE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_EXPECTED_PARAMETER_SIZE = "expectedParameterSize";
    /**
     * The constant PARAMETER_EXPECTED_PARAMETER_TYPE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_EXPECTED_PARAMETER_TYPE = "expectedParameterType";
    /**
     * The constant PARAMETER_FEATURE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_FEATURE = "feature";
    /**
     * The constant PARAMETER_HEAP_STATISTICS.
     *
     * @since 1.0.4
     */
    public static final String PARAMETER_HEAP_STATISTICS = "heapStatistics";
    /**
     * The constant PARAMETER_LINE_NUMBER.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_LINE_NUMBER = "lineNumber";
    /**
     * The constant PARAMETER_LOCKED_THREAD_ID.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_LOCKED_THREAD_ID = "lockedThreadId";
    /**
     * The constant PARAMETER_MAX_DEPTH.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_MAX_DEPTH = "maxDepth";
    /**
     * The constant PARAMETER_MESSAGE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_MESSAGE = "message";
    /**
     * The constant PARAMETER_METHOD_NAME.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_METHOD_NAME = "methodName";
    /**
     * The constant PARAMETER_V8_MODULE_COUNT.
     *
     * @since 3.0.3
     */
    public static final String PARAMETER_V8_MODULE_COUNT = "v8ModuleCount";
    /**
     * The constant PARAMETER_REFERENCE_COUNT.
     *
     * @since 3.0.3
     */
    public static final String PARAMETER_REFERENCE_COUNT = "referenceCount";
    /**
     * The constant PARAMETER_CALLBACK_CONTEXT_COUNT.
     *
     * @since 3.0.3
     */
    public static final String PARAMETER_CALLBACK_CONTEXT_COUNT = "callbackContextCount";
    /**
     * The constant PARAMETER_OS.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_OS = "OS";
    /**
     * The constant PARAMETER_PATH.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_PATH = "path";
    /**
     * The constant PARAMETER_REASON.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_REASON = "reason";
    /**
     * The constant PARAMETER_RESOURCE_NAME.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_RESOURCE_NAME = "resourceName";
    /**
     * The constant PARAMETER_SOURCE_LINE.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_SOURCE_LINE = "sourceLine";
    /**
     * The constant PARAMETER_START_COLUMN.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_START_COLUMN = "startColumn";
    /**
     * The constant PARAMETER_START_POSITION.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_START_POSITION = "startPosition";
    /**
     * The constant PARAMETER_SYMBOL.
     *
     * @since 0.8.5
     */
    public static final String PARAMETER_SYMBOL = "symbol";
    /**
     * The constant OSNotSupported.
     *
     * @since 0.8.5
     */
    public static final JavetError OSNotSupported = new JavetError(
            101, JavetErrorType.System, "OS ${OS} is not supported");
    /**
     * The constant LibraryNotFound.
     *
     * @since 0.8.5
     */
    public static final JavetError LibraryNotFound = new JavetError(
            102, JavetErrorType.System, "Javet library ${path} is not found");
    /**
     * The constant LibraryNotLoaded.
     *
     * @since 0.8.5
     */
    public static final JavetError LibraryNotLoaded = new JavetError(
            103, JavetErrorType.System, "Javet library is not loaded because ${reason}");
    /**
     * The constant NotSupported.
     *
     * @since 0.8.5
     */
    public static final JavetError NotSupported = new JavetError(
            104, JavetErrorType.System, "${feature} is not supported");
    /**
     * The constant FailedToReadPath.
     *
     * @since 0.8.5
     */
    public static final JavetError FailedToReadPath = new JavetError(
            105, JavetErrorType.System, "Failed to read ${path}");
    /**
     * The constant CompilationFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError CompilationFailure = new JavetError(
            201, JavetErrorType.Compilation, "${message}");
    /**
     * The constant ExecutionFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError ExecutionFailure = new JavetError(
            301, JavetErrorType.Execution, "${message}");
    /**
     * The constant ExecutionTerminated.
     *
     * @since 0.8.5
     */
    public static final JavetError ExecutionTerminated = new JavetError(
            302, JavetErrorType.Execution, "Execution is terminated and continuable is ${continuable}");
    /**
     * The constant CallbackSignatureParameterSizeMismatch.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackSignatureParameterSizeMismatch = new JavetError(
            401, JavetErrorType.Callback, "Callback signature mismatches: method name is ${methodName}, expected parameter size is ${expectedParameterSize}, actual parameter size is ${actualParameterSize}");
    /**
     * The constant CallbackSignatureParameterTypeMismatch.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackSignatureParameterTypeMismatch = new JavetError(
            402, JavetErrorType.Callback, "Callback signature mismatches: expected parameter type is ${expectedParameterType}, actual parameter type is ${actualParameterType}");
    /**
     * The constant CallbackInjectionFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackInjectionFailure = new JavetError(
            403, JavetErrorType.Callback, "Failed to inject runtime with error message ${message}");
    /**
     * The constant CallbackRegistrationFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackRegistrationFailure = new JavetError(
            404, JavetErrorType.Callback, "Callback ${methodName} registration failed with error message ${message}");
    /**
     * The constant CallbackMethodFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackMethodFailure = new JavetError(
            405, JavetErrorType.Callback, "Callback ${methodName} failed with error message ${message}");
    /**
     * The constant CallbackUnknownFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackUnknownFailure = new JavetError(
            406, JavetErrorType.Callback, "Callback failed with unknown error message ${message}");
    /**
     * The constant CallbackUnregistrationFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError CallbackUnregistrationFailure = new JavetError(
            407, JavetErrorType.Callback, "Callback ${methodName} unregistration failed with error message ${message}");
    /**
     * The constant CallbackTypeNotSupported.
     *
     * @since 2.2.0
     */
    public static final JavetError CallbackTypeNotSupported = new JavetError(
            408, JavetErrorType.Callback, "Callback type ${callbackType} is not supported");
    /**
     * The constant ConverterFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError ConverterFailure = new JavetError(
            501, JavetErrorType.Converter, "Failed to convert values with error message ${message}");
    /**
     * The constant ConverterCircularStructure.
     *
     * @since 0.8.5
     */
    public static final JavetError ConverterCircularStructure = new JavetError(
            502, JavetErrorType.Converter, "Circular structure is detected with max depth ${maxDepth} reached");
    /**
     * The constant ConverterSymbolNotBuiltIn.
     *
     * @since 0.8.5
     */
    public static final JavetError ConverterSymbolNotBuiltIn = new JavetError(
            503, JavetErrorType.Converter, "${symbol} is not a built-in symbol");
    /**
     * The constant ModuleNameEmpty.
     *
     * @since 0.8.5
     */
    public static final JavetError ModuleNameEmpty = new JavetError(
            601, JavetErrorType.Module, "Module name is empty");
    /**
     * The constant ModuleNotFound.
     *
     * @since 0.8.5
     */
    public static final JavetError ModuleNotFound = new JavetError(
            602, JavetErrorType.Module, "Module ${moduleName} is not found");
    /**
     * The constant ModulePermissionDenied.
     *
     * @since 0.8.5
     */
    public static final JavetError ModulePermissionDenied = new JavetError(
            603, JavetErrorType.Module, "Denied access to module ${moduleName}");
    /**
     * The constant LockAcquisitionFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError LockAcquisitionFailure = new JavetError(
            701, JavetErrorType.Lock, "Failed to acquire the lock");
    /**
     * The constant LockReleaseFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError LockReleaseFailure = new JavetError(
            702, JavetErrorType.Lock, "Failed to release the lock");
    /**
     * The constant LockConflictThreadIdMismatch.
     *
     * @since 0.8.5
     */
    public static final JavetError LockConflictThreadIdMismatch = new JavetError(
            703, JavetErrorType.Lock, "Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}");
    /**
     * The constant RuntimeAlreadyClosed.
     *
     * @since 0.8.5
     */
    public static final JavetError RuntimeAlreadyClosed = new JavetError(
            801, JavetErrorType.Runtime, "Runtime is already closed");
    /**
     * The constant RuntimeAlreadyRegistered.
     *
     * @since 0.8.5
     */
    public static final JavetError RuntimeAlreadyRegistered = new JavetError(
            802, JavetErrorType.Runtime, "Runtime is already registered");
    /**
     * The constant RuntimeNotRegistered.
     *
     * @since 0.8.5
     */
    public static final JavetError RuntimeNotRegistered = new JavetError(
            803, JavetErrorType.Runtime, "Runtime is not registered");
    /**
     * The constant RuntimeLeakageDetected.
     *
     * @since 0.8.5
     */
    public static final JavetError RuntimeLeakageDetected = new JavetError(
            804, JavetErrorType.Runtime, "${count} runtime(s) leakage is detected");
    /**
     * The constant RuntimeCloseFailure.
     *
     * @since 0.8.5
     */
    public static final JavetError RuntimeCloseFailure = new JavetError(
            805, JavetErrorType.Runtime, "Failed to close the runtime with error message ${message}");
    /**
     * The constant RuntimeOutOfMemory.
     *
     * @since 1.0.4
     */
    public static final JavetError RuntimeOutOfMemory = new JavetError(
            806, JavetErrorType.Runtime, "Runtime is out of memory because ${message} with ${heapStatistics}");
    /**
     * The constant RuntimeCreateSnapshotDisabled.
     *
     * @since 3.0.3
     */
    public static final JavetError RuntimeCreateSnapshotDisabled = new JavetError(
            807, JavetErrorType.Runtime, "Runtime create snapshot is disabled");
    /**
     * The constant RuntimeCreateSnapshotBlocked.
     *
     * @since 3.0.3
     */
    public static final JavetError RuntimeCreateSnapshotBlocked = new JavetError(
            808, JavetErrorType.Runtime, "Runtime create snapshot is blocked because of " +
            "${callbackContextCount} callback context(s), " +
            "${referenceCount} reference(s), " +
            "${v8ModuleCount} module(s)");
    /**
     * The constant EngineNotAvailable.
     *
     * @since 1.1.6
     */
    public static final JavetError EngineNotAvailable = new JavetError(
            901, JavetErrorType.Engine, "Engine is not available.");
    /**
     * The Code.
     *
     * @since 0.8.5
     */
    protected int code;
    /**
     * The Format.
     *
     * @since 0.8.5
     */
    protected String format;
    /**
     * The Type.
     *
     * @since 0.8.5
     */
    protected JavetErrorType type;

    /**
     * Instantiates a new Javet error.
     *
     * @param code   the code
     * @param type   the type
     * @param format the format
     * @since 0.8.5
     */
    JavetError(int code, JavetErrorType type, String format) {
        this.code = code;
        this.format = format;
        this.type = type;
    }

    /**
     * Gets code.
     *
     * @return the code
     * @since 0.8.5
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets format.
     *
     * @return the format
     * @since 0.8.5
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets message.
     *
     * @param parameters the parameters
     * @return the message
     * @since 0.8.5
     */
    public String getMessage(Map<String, Object> parameters) {
        return SimpleFreeMarkerFormat.format(format, parameters);
    }

    /**
     * Gets type.
     *
     * @return the type
     * @since 0.8.5
     */
    public JavetErrorType getType() {
        return type;
    }
}
