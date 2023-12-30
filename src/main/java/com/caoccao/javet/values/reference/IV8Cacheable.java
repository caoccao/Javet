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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;

/**
 * The interface V8 cacheable.
 *
 * @since 2.2.0
 */
public interface IV8Cacheable extends IV8ValueReference {
    /**
     * Gets cached data.
     *
     * @return the cached data
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    byte[] getCachedData() throws JavetException;
}
