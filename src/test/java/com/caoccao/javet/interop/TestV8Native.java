/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestV8Native {
    @Test
    public void testGetVersion() throws JavetOSNotSupportedException {
        JavetLibLoader.load();
        String versionString = V8Native.getVersion();
        assertNotNull(versionString);
        assertTrue(versionString.startsWith("8."));
    }
}
