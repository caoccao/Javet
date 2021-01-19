/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.exceptions;

public class JavetException extends Exception {
    public JavetException() {
        super();
    }

    public JavetException(String message) {
        super(message);
    }

    public JavetException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavetException(Throwable cause) {
        super(cause);
    }

    public JavetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
