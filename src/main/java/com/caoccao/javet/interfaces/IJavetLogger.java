package com.caoccao.javet.interfaces;

import java.text.MessageFormat;

public interface IJavetLogger {
    void debug(String message);

    void error(String message);

    void error(String message, Throwable cause);

    void info(String message);

    default void logDebug(String format, Object... objects) {
        debug(MessageFormat.format(format, objects));
    }

    default void logError(Throwable cause, String format, Object... objects) {
        error(MessageFormat.format(format, objects), cause);
    }

    default void logError(String format, Object... objects) {
        error(MessageFormat.format(format, objects));
    }

    default void logInfo(String format, Object... objects) {
        info(MessageFormat.format(format, objects));
    }

    default void logWarn(String format, Object... objects) {
        warn(MessageFormat.format(format, objects));
    }

    void warn(String message);
}
