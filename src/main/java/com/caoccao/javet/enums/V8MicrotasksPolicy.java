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

package com.caoccao.javet.enums;

import java.util.stream.Stream;

/**
 * The enum V8 microtasks policy represents how the microtasks of a V8 isolate are invoked.
 * <p>
 * The ids are aligned with <code>v8::MicrotasksPolicy</code>.
 *
 * @since 6.0.1
 */
public enum V8MicrotasksPolicy {
    /**
     * Microtasks are only invoked by an explicit microtask checkpoint.
     * <p>
     * Nothing drains the microtask queue automatically, so the promise jobs stay pending
     * until the checkpoint is performed. It allows a batch of calls to be made against the
     * V8 runtime before the pending promise jobs are drained in one go.
     *
     * @since 6.0.1
     */
    Explicit(0, "Explicit"),
    /**
     * Microtasks invocation is controlled by <code>v8::MicrotasksScope</code> objects.
     * <p>
     * Javet never creates a <code>v8::MicrotasksScope</code>, hence this policy is not supported.
     * It is defined here only so that the policy reported by V8 can always be represented.
     *
     * @since 6.0.1
     */
    Scoped(1, "Scoped"),
    /**
     * Microtasks are invoked when the JavaScript call depth decrements to zero.
     * <p>
     * It is the default policy in the V8 mode. Every call made against the V8 runtime is a
     * call depth boundary of its own, so the microtask queue is drained as soon as that call
     * returns.
     *
     * @since 6.0.1
     */
    Auto(2, "Auto");

    private static final int LENGTH = 3;
    private static final V8MicrotasksPolicy[] POLICIES = new V8MicrotasksPolicy[LENGTH];

    static {
        Stream.of(values()).forEach(v -> POLICIES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    V8MicrotasksPolicy(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse microtasks policy from id.
     *
     * @param id the id
     * @return the V8 microtasks policy
     * @since 6.0.1
     */
    public static V8MicrotasksPolicy parse(int id) {
        return id >= 0 && id < LENGTH ? POLICIES[id] : Auto;
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 6.0.1
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 6.0.1
     */
    public String getName() {
        return name;
    }
}
