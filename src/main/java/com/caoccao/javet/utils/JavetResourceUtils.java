/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueReference;
import com.caoccao.javet.values.reference.V8ValueReference;

import java.util.Collection;

public final class JavetResourceUtils {
    private JavetResourceUtils() {
    }

    public static void safeClose(Object... objects) {
        for (Object object : objects) {
            safeClose(object);
        }
    }

    public static void safeClose(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof IV8ValueReference) {
            try {
                IV8ValueReference iV8ValueReference = (IV8ValueReference) object;
                if (iV8ValueReference.getHandle() != V8ValueReference.INVALID_HANDLE) {
                    iV8ValueReference.close();
                }
            } catch (JavetException ignored) {
            }
        } else if (object instanceof IJavetClosable) {
            try {
                ((IJavetClosable) object).close();
            } catch (JavetException ignored) {
            }
        } else if (object instanceof V8Value[]) {
            for (V8Value v8Value : (V8Value[]) object) {
                safeClose(v8Value);
            }
        } else if (object.getClass().isArray()) {
            for (Object o : (Object[]) object) {
                safeClose(o);
            }
        } else if (object instanceof Collection) {
            for (Object o : (Collection<?>) object) {
                safeClose(o);
            }
        }
    }
}
