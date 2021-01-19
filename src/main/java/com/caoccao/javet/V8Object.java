/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet;

import com.caoccao.javet.interfaces.JavetClosable;

public abstract class V8Object implements JavetClosable {

    @Override
    public void close() throws RuntimeException {
    }
}
