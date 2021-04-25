package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.SimpleFreeMarkerFormat;

import java.util.Map;

public class JavetError {
    public static final JavetError NotSupported = new JavetError(
            11, "${feature} is not supported");
    public static final JavetError OSNotSupported = new JavetError(
            12, "OS ${OS} is not supported");

    public static final JavetError CompilationFailure = new JavetError(
            21, "${message}");

    public static final JavetError ExecutionFailure = new JavetError(
            31, "${message}");
    public static final JavetError ExecutionTerminated = new JavetError(
            32, "Execution is terminated and continuable is ${continuable}");
    public static final JavetError CallbackFailure = new JavetError(
            33, "${message}");

    public static final JavetError CallbackNotRegistered = new JavetError(
            41, "Callback is not registered");
    public static final JavetError CallbackSignatureParameterSizeMismatch = new JavetError(
            42, "Callback signature mismatches: method name is ${methodName}, expected parameter size is ${expectedParameterSize}, actual parameter size is ${actualParameterSize}");
    public static final JavetError CallbackSignatureParameterTypeMismatch = new JavetError(
            43, "Callback signature mismatches: expected parameter type is ${expectedParameterType}, actual parameter type is ${actualParameterType}");
    public static final JavetError CallbackMethodNotFound = new JavetError(
            44, "Callback method is not found with error message ${message}");

    public static final JavetError ConverterFailure = new JavetError(
            51, "Failed to convert values with error message ${message}");

    public static final JavetError ModuleNameEmpty = new JavetError(
            61, "Module name is empty");

    public static final JavetError LockAcquisitionFailure = new JavetError(
            71, "Failed to acquire the lock");
    public static final JavetError LockReleaseFailure = new JavetError(
            72, "Failed to release the lock");
    public static final JavetError LockConflictThreadIdMismatch = new JavetError(
            73, "Runtime lock conflict is detected with locked thread ID ${lockedThreadID} and current thread ID ${currentThreadID}");

    public static final JavetError RuntimeAlreadyClosed = new JavetError(
            81, "Runtime is already closed");
    public static final JavetError RuntimeAlreadyRegistered = new JavetError(
            82, "Runtime is already registered");
    public static final JavetError RuntimeLeakageDetected = new JavetError(
            83, "${count} runtime(s) leakage is detected");
    public static final JavetError RuntimeNotRegistered = new JavetError(
            84, "Runtime is not registered");

    public static final JavetError LibraryNotFound = new JavetError(
            101, "Javet library ${path} is not found");
    public static final JavetError LibraryNotLoaded = new JavetError(
            102, "Javet library is not loaded because ${reason}");

    public static final JavetError FailedToReadPath = new JavetError(
            1001, "Failed to read ${path}");

    protected int code;
    protected String format;

    JavetError(int code, String format) {
        this.code = code;
        this.format = format;
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
}
