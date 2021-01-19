/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.exceptions;

import java.text.MessageFormat;

public class JavetOSNotSupportedException extends JavetException {
    public JavetOSNotSupportedException(String osName) {
        super(MessageFormat.format("OS [{0}] is not supported.", osName));
    }
}
