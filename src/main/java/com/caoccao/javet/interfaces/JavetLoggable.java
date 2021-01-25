package com.caoccao.javet.interfaces;

import java.text.MessageFormat;
import java.util.logging.Logger;

public interface JavetLoggable {
    Logger getLogger();

    default void logWarn(String format, Object... objects) {
        getLogger().warning(MessageFormat.format(format, objects));
    }
}
