package com.caoccao.javet.exceptions;

import com.caoccao.javet.enums.JavetErrorType;
import com.caoccao.javet.utils.SimpleFreeMarkerFormat;

import java.util.Map;

public class JavetError {
    public static final String PARAMETER_EXPECTED_PARAMETER_TYPE = "expectedParameterType";
    public static final String PARAMETER_ACTUAL_PARAMETER_TYPE = "actualParameterType";
    public static final String PARAMETER_METHOD_NAME = "methodName";
    public static final String PARAMETER_EXPECTED_PARAMETER_SIZE = "expectedParameterSize";
    public static final String PARAMETER_ACTUAL_PARAMETER_SIZE = "actualParameterSize";

    public static final String PARAMETER_RESOURCE_NAME = "resourceName";
    public static final String PARAMETER_SOURCE_LINE = "sourceLine";
    public static final String PARAMETER_LINE_NUMBER = "lineNumber";
    public static final String PARAMETER_START_COLUMN = "startColumn";
    public static final String PARAMETER_END_COLUMN = "endColumn";
    public static final String PARAMETER_START_POSITION = "startPosition";
    public static final String PARAMETER_END_POSITION = "endPosition";
    public static final String PARAMETER_MESSAGE = "message";

    public static final String PARAMETER_CURRENT_THREAD_ID = "currentThreadId";
    public static final String PARAMETER_LOCKED_THREAD_ID = "lockedThreadId";

    public static final String PARAMETER_OS = "OS";

    public static final String PARAMETER_FEATURE = "feature";

    public static final String PARAMETER_COUNT = "count";
    public static final String PARAMETER_CONTINUABLE = "continuable";
    public static final String PARAMETER_PATH = "path";
    public static final String PARAMETER_REASON = "reason";

    public static final JavetError OSNotSupported = new JavetError(
            101, JavetErrorType.System, "OS ${OS} is not supported");
    public static final JavetError LibraryNotFound = new JavetError(
            102, JavetErrorType.System, "Javet library ${path} is not found");
    public static final JavetError LibraryNotLoaded = new JavetError(
            103, JavetErrorType.System, "Javet library is not loaded because ${reason}");
    public static final JavetError NotSupported = new JavetError(
            104, JavetErrorType.System, "${feature} is not supported");
    public static final JavetError FailedToReadPath = new JavetError(
            105, JavetErrorType.System, "Failed to read ${path}");

    public static final JavetError CompilationFailure = new JavetError(
            201, JavetErrorType.Compilation, "${message}");

    public static final JavetError ExecutionFailure = new JavetError(
            301, JavetErrorType.Execution, "${message}");
    public static final JavetError ExecutionTerminated = new JavetError(
            302, JavetErrorType.Execution, "Execution is terminated and continuable is ${continuable}");

    public static final JavetError CallbackSignatureParameterSizeMismatch = new JavetError(
            401, JavetErrorType.Callback, "Callback signature mismatches: method name is ${methodName}, expected parameter size is ${expectedParameterSize}, actual parameter size is ${actualParameterSize}");
    public static final JavetError CallbackSignatureParameterTypeMismatch = new JavetError(
            402, JavetErrorType.Callback, "Callback signature mismatches: expected parameter type is ${expectedParameterType}, actual parameter type is ${actualParameterType}");
    public static final JavetError CallbackInjectionFailure = new JavetError(
            403, JavetErrorType.Callback, "Failed to inject runtime with error message ${message}");
    public static final JavetError CallbackRegistrationFailure = new JavetError(
            404, JavetErrorType.Callback, "Callback ${methodName} registration failed with error message ${message}");

    public static final JavetError ConverterFailure = new JavetError(
            501, JavetErrorType.Converter, "Failed to convert values with error message ${message}");

    public static final JavetError ModuleNameEmpty = new JavetError(
            601, JavetErrorType.Module, "Module name is empty");

    public static final JavetError LockAcquisitionFailure = new JavetError(
            701, JavetErrorType.Lock, "Failed to acquire the lock");
    public static final JavetError LockReleaseFailure = new JavetError(
            702, JavetErrorType.Lock, "Failed to release the lock");
    public static final JavetError LockConflictThreadIdMismatch = new JavetError(
            703, JavetErrorType.Lock, "Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}");

    public static final JavetError RuntimeAlreadyClosed = new JavetError(
            801, JavetErrorType.Runtime, "Runtime is already closed");
    public static final JavetError RuntimeAlreadyRegistered = new JavetError(
            802, JavetErrorType.Runtime, "Runtime is already registered");
    public static final JavetError RuntimeNotRegistered = new JavetError(
            803, JavetErrorType.Runtime, "Runtime is not registered");
    public static final JavetError RuntimeLeakageDetected = new JavetError(
            804, JavetErrorType.Runtime, "${count} runtime(s) leakage is detected");

    protected int code;
    protected String format;
    protected JavetErrorType type;

    JavetError(int code, JavetErrorType type, String format) {
        this.code = code;
        this.format = format;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getFormat() {
        return format;
    }

    public String getMessage(Map<String, Object> parameters) {
        return SimpleFreeMarkerFormat.format(format, parameters);
    }

    public JavetErrorType getType() {
        return type;
    }
}
