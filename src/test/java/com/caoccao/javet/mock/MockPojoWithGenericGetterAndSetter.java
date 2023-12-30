/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.mock;

import java.util.HashMap;
import java.util.Map;

public class MockPojoWithGenericGetterAndSetter extends MockPojo {
    protected Map<String, String> map;

    public MockPojoWithGenericGetterAndSetter() {
        super();
        map = new HashMap<>();
    }

    public String get(String key) {
        return map.get(key);
    }

    public void set(String key, String value) {
        map.put(key, value);
    }
}
