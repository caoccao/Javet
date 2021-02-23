package com.caoccao.javet.utils;

import com.caoccao.javet.interfaces.IJavetLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavetDefaultLogger implements IJavetLogger {
    protected Logger logger;
    protected String name;

    public JavetDefaultLogger(String name) {
        logger = Logger.getLogger(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void debug(String message) {
        logger.log(Level.FINE, message);
    }

    @Override
    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Throwable cause) {
        logger.severe(message);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
                cause.printStackTrace(printStream);
                logger.severe(byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()));
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }
}
