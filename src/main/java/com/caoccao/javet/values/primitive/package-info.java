/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

/**
 * V8 primitive value types that do not hold native handles and are safe to use without closing.
 * <ul>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueString} - JavaScript string.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueInteger} - 32-bit integer.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueLong} - 64-bit long (BigInt).</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueDouble} - Double-precision float.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueBoolean} - Boolean true/false.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueBigInteger} - Arbitrary-precision BigInteger.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueNull} - JavaScript {@code null}.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueUndefined} - JavaScript {@code undefined}.</li>
 *   <li>{@link com.caoccao.javet.values.primitive.V8ValueZonedDateTime} - JavaScript Date as {@code ZonedDateTime}.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.values.primitive;