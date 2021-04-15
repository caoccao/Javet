package com.caoccao.javet.exceptions;

import java.text.MessageFormat;

public class JavetCallbackException extends JavetException {
    public JavetCallbackException(String message) {
        super(message);
    }

    public JavetCallbackException(String format, Object... objects) {
        this(MessageFormat.format(format, objects));
    }
}
