/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLoggable;
import com.caoccao.javet.interfaces.IJavetResettable;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.reference.IV8ValueCollection;
import com.caoccao.javet.values.reference.V8ValueReference;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public final class V8Runtime
        implements IJavetClosable, IJavetResettable, IJavetLoggable {
    private static final long INVALID_THREAD_ID = -1L;
    private String globalName;
    private long handle;
    private Logger logger;
    private Map<Long, V8ValueReference> referenceMap;
    private long threadId;
    private V8Host v8Host;

    V8Runtime(V8Host v8Host, long handle, String globalName) {
        this.globalName = globalName;
        this.handle = handle;
        logger = Logger.getLogger(getClass().getName());
        referenceMap = new TreeMap<>();
        this.threadId = INVALID_THREAD_ID;
        this.v8Host = v8Host;
    }

    public long getHandle() {
        return handle;
    }

    public void checkLock() throws JavetV8RuntimeLockConflictException {
        final long currentThreadId = Thread.currentThread().getId();
        if (threadId != currentThreadId) {
            throw new JavetV8RuntimeLockConflictException(threadId, currentThreadId);
        }
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public void lock() throws JavetV8RuntimeLockConflictException {
        if (threadId == INVALID_THREAD_ID) {
            V8Native.lockV8Runtime(handle);
            threadId = Thread.currentThread().getId();
        } else {
            checkLock();
        }
    }

    public void unlock() throws JavetV8RuntimeLockConflictException {
        checkLock();
        threadId = INVALID_THREAD_ID;
        V8Native.unlockV8Runtime(handle);
    }

    public <T extends V8Value> T execute(String scriptString) throws JavetException {
        return execute(scriptString, new V8ScriptOrigin(), true);
    }

    public <T extends V8Value> T execute(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return execute(scriptString, v8ScriptOrigin, true);
    }

    public <T extends V8Value> T execute(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired)
            throws JavetException {
        T returnValue = (T) V8Native.execute(handle, scriptString,
                resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
        if (returnValue != null) {
            returnValue.setV8Runtime(this);
        }
        return returnValue;
    }

    public Integer executeInteger(String scriptString) throws JavetException {
        return executeInteger(scriptString, new V8ScriptOrigin());
    }

    public Integer executeInteger(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    public Long executeLong(String scriptString) throws JavetException {
        return executeLong(scriptString, new V8ScriptOrigin());
    }

    public Long executeLong(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    public String executeString(String scriptString) throws JavetException {
        return executeString(scriptString, new V8ScriptOrigin());
    }

    public String executeString(String scriptString, V8ScriptOrigin v8ScriptOrigin)
            throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    private <R extends Object, T extends V8ValuePrimitive<R>> R executeObject(String scriptString)
            throws JavetException {
        return executeObject(scriptString, new V8ScriptOrigin());
    }

    private <R extends Object, T extends V8ValuePrimitive<R>> R executeObject(String scriptString, V8ScriptOrigin v8ScriptOrigin)
            throws JavetException {
        V8Value v8Value = execute(scriptString, v8ScriptOrigin, true);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    public void executeVoid(String scriptString) throws JavetException {
        executeVoid(scriptString, new V8ScriptOrigin());
    }

    public void executeVoid(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        execute(scriptString, v8ScriptOrigin, false);
    }

    public int getLength(IV8ValueCollection iV8ValueCollection) {
        return V8Native.getLength(handle, iV8ValueCollection.getHandle(), iV8ValueCollection.getType());
    }

    @Override
    public void close() {
        if (threadId != INVALID_THREAD_ID) {
            try {
                unlock();
            } catch (JavetV8RuntimeLockConflictException e) {
            }
        }
        if (!referenceMap.isEmpty()) {
            logWarn("{0} V8 object(s) not recycled.", referenceMap.size());
            removeReferenceHandles();
        }
        v8Host.closeV8Runtime(this);
        handle = 0L;
    }

    @Override
    public void reset() {
        V8Native.resetV8Runtime(handle, globalName);
    }

    public void addReferenceHandle(V8ValueReference v8ValueReference) {
        referenceMap.put(v8ValueReference.getHandle(), v8ValueReference);
    }

    public void removeReferenceHandle(V8ValueReference v8ValueReference) {
        final long referenceHandle = v8ValueReference.getHandle();
        if (referenceMap.containsKey(referenceHandle)) {
            V8Native.removeReferenceHandle(referenceHandle);
            referenceMap.remove(referenceHandle);
        }
    }

    private void removeReferenceHandles() {
        for (Long referenceHandle : referenceMap.keySet()) {
            V8Native.removeReferenceHandle(referenceHandle);
        }
        referenceMap.clear();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
