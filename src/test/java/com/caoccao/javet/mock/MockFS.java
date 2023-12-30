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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValuePromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MockFS implements AutoCloseable, Runnable {
    protected Thread daemonThread;
    protected Map<String, String> fileMap;
    protected ConcurrentLinkedQueue<Task> queue;
    protected volatile boolean quitting;
    protected long timeout;
    protected V8Runtime v8Runtime;

    public MockFS(long timeout) {
        fileMap = new ConcurrentHashMap<>();
        queue = new ConcurrentLinkedQueue<>();
        quitting = false;
        this.timeout = timeout;
        v8Runtime = null;
        daemonThread = new Thread(this);
        daemonThread.setName("MockFS Daemon");
        daemonThread.start();
    }

    @Override
    public void close() throws Exception {
        quitting = true;
        daemonThread.join();
    }

    public Map<String, String> getFileMap() {
        return fileMap;
    }

    public long getTimeout() {
        return timeout;
    }

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @V8Function
    public V8ValuePromise readFileAsync(String filePath) throws JavetException {
        V8ValuePromise v8ValuePromiseResolver = v8Runtime.createV8ValuePromise();
        queue.add(new Task(v8ValuePromiseResolver, filePath, timeout));
        return v8ValuePromiseResolver.getPromise();
    }

    @V8Function
    public String readFileSync(String filePath) {
        return fileMap.get(filePath);
    }

    @Override
    public void run() {
        while (!quitting) {
            final int length = queue.size();
            for (int i = 0; i < length; ++i) {
                Task task = queue.poll();
                if (task == null) {
                    break;
                }
                if (System.currentTimeMillis() >= task.getTick() + task.getTimeout()) {
                    String fileContent = fileMap.get(task.getFilePath());
                    try (V8ValuePromise promise = task.getPromise()) {
                        if (fileContent == null) {
                            promise.reject(v8Runtime.createV8ValueUndefined());
                        } else {
                            promise.resolve(fileContent);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                    }
                    if (v8Runtime.getJSRuntimeType().isNode()) {
                        v8Runtime.await();
                    }
                } else {
                    queue.add(task);
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
                break;
            }
        }
        while (!queue.isEmpty()) {
            Task task = queue.poll();
            if (task == null) {
                break;
            }
            try (V8ValuePromise promise = task.getPromise()) {
                promise.reject(v8Runtime.createV8ValueUndefined());
            } catch (JavetException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @V8RuntimeSetter
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

    static class Task {
        private final String filePath;
        private final V8ValuePromise promise;
        private final long tick;
        private final long timeout;

        public Task(V8ValuePromise promise, String filePath, long timeout) {
            this.filePath = filePath;
            this.promise = promise;
            this.tick = System.currentTimeMillis();
            this.timeout = timeout;
        }

        public String getFilePath() {
            return filePath;
        }

        public V8ValuePromise getPromise() {
            return promise;
        }

        public long getTick() {
            return tick;
        }

        public long getTimeout() {
            return timeout;
        }
    }
}
