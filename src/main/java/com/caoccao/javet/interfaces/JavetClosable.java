/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.interfaces;

public interface JavetClosable extends AutoCloseable {
    void close() throws RuntimeException;
}
