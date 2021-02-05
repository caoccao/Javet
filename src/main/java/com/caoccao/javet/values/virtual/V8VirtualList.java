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

package com.caoccao.javet.values.virtual;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("unchecked")
public class V8VirtualList<T extends Object> extends ArrayList<T> implements IJavetClosable {
    public V8VirtualList(int initialCapacity) {
        super(initialCapacity);
    }

    public V8VirtualList(Collection<? extends T> c) {
        super(c);
    }

    public V8VirtualList() {
        super();
    }

    @Override
    public void close() throws JavetException {
        if (!isEmpty()) {
            for (Object value : this) {
                if (value instanceof IJavetClosable) {
                    ((IJavetClosable) value).close();
                }
            }
        }
    }
}
