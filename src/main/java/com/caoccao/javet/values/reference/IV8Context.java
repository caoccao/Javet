/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

package com.caoccao.javet.values.reference;

/**
 * The interface V8 context.
 *
 * @since 2.0.1
 */
public interface IV8Context extends IV8ValueReference {
    /**
     * Gets length of the elements.
     *
     * @return the length
     * @since 2.0.1
     */
    int getLength();

    /**
     * Sets length of the elements.
     *
     * @param length the length
     * @since 2.0.1
     */
    void setLength(int length);
}
