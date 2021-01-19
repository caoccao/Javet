/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestJavetLibLoader {
    @Test
    public void testLoad() throws JavetOSNotSupportedException {
        assertTrue(JavetLibLoader.load());
    }
}
