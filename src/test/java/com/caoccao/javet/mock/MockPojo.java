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

public class MockPojo {
    public static final String STATIC_READONLY_VALUE = "1";
    public static String STATIC_WRITABLE_VALUE = "1";
    public final String instanceReadonlyValue = "1";
    public String instanceWritableValue = "1";
    protected int intValue;
    protected String name;
    protected String stringValue;

    public static int staticAdd(int a, int b) {
        return a + b;
    }

    public int add(int a, int b) {
        return a + b;
    }

    public int add(int... numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number * 2;
        }
        return sum;
    }

    public Double add(Double a, Double b) {
        return a + b;
    }

    public String concat(String prefix, Object... objects) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (Object object : objects) {
            sb.append(", ").append(object);
        }
        return sb.toString();
    }

    public int getIntValue() {
        return intValue;
    }

    public String getName() {
        return name;
    }

    public String getSSSStringValue() {
        return stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSSSStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
