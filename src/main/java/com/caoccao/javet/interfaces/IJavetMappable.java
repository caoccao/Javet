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

package com.caoccao.javet.interfaces;

import java.util.Map;

/**
 * The interface Javet mappable.
 *
 * @since 0.9.14
 */
public interface IJavetMappable {
    /**
     * From map.
     *
     * @param map the map
     * @since 0.9.14
     */
    void fromMap(Map<String, Object> map);

    /**
     * To map map.
     *
     * @return the map
     * @since 0.9.14
     */
    Map<String, Object> toMap();
}
