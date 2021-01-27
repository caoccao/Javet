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
import com.caoccao.javet.exceptions.JavetV8RuntimeAlreadyRegisteredException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLoggable;
import com.caoccao.javet.interfaces.IJavetResettable;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueCollection;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueReference;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public final class V8Runtime
        implements IJavetClosable, IJavetResettable, IJavetLoggable, IV8Executable {
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
        threadId = INVALID_THREAD_ID;
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

    public boolean isLocked() {
        return threadId != INVALID_THREAD_ID;
    }

    public void lock() throws JavetV8RuntimeLockConflictException {
        if (!isLocked()) {
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

    @Override
    public <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        checkLock();
        return decorateV8Value((T) V8Native.execute(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
    }

    public boolean containsKey(IV8ValueObject iV8ValueObject, int key) throws JavetException {
        checkLock();
        return V8Native.containsKey(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key);
    }

    public boolean containsKey(IV8ValueObject iV8ValueObject, String key) throws JavetException {
        checkLock();
        return V8Native.containsKey(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key);
    }

    public int getLength(IV8ValueCollection iV8ValueCollection) throws JavetException {
        checkLock();
        return V8Native.getLength(handle, iV8ValueCollection.getHandle(), iV8ValueCollection.getType());
    }

    public IV8ValueCollection getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueArray) V8Native.getOwnPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType()));
    }

    public IV8ValueCollection getPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueArray) V8Native.getPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType()));
    }

    public int getSize(IV8ValueObject iV8ValueObject) throws JavetException {
        checkLock();
        return V8Native.getSize(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType());
    }

    public <T extends V8Value> T getValue(
            IV8ValueObject iV8ValueObject, int index) throws JavetException {
        checkLock();
        return decorateV8Value((T) V8Native.getValue(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), index));
    }

    public <T extends V8Value> T getValue(
            IV8ValueObject iV8ValueObject, String key) throws JavetException {
        checkLock();
        return decorateV8Value((T) V8Native.getValue(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key));
    }

    @Override
    public void close() throws JavetV8RuntimeLockConflictException {
        if (!referenceMap.isEmpty()) {
            logWarn("{0} V8 object(s) not recycled.", referenceMap.size());
            if (!isLocked()) {
                lock();
                removeReferences();
            }
        }
        if (isLocked()) {
            try {
                unlock();
            } catch (JavetV8RuntimeLockConflictException e) {
            }
        }
        v8Host.closeV8Runtime(this);
        handle = 0L;
    }

    @Override
    public void reset() {
        V8Native.resetV8Runtime(handle, globalName);
    }

    private <T extends V8Value> T decorateV8Value(T v8Value)
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyRegisteredException {
        if (v8Value != null) {
            v8Value.setV8Runtime(this);
        }
        return v8Value;
    }

    public void addReference(V8ValueReference v8ValueReference) {
        referenceMap.put(v8ValueReference.getHandle(), v8ValueReference);
    }

    public void removeReference(V8ValueReference v8ValueReference) throws JavetV8RuntimeLockConflictException {
        checkLock();
        final long referenceHandle = v8ValueReference.getHandle();
        if (referenceMap.containsKey(referenceHandle)) {
            V8Native.removeReferenceHandle(referenceHandle);
            referenceMap.remove(referenceHandle);
        }
    }

    private void removeReferences() throws JavetV8RuntimeLockConflictException {
        checkLock();
        for (Long referenceHandle : referenceMap.keySet()) {
            V8Native.removeReferenceHandle(referenceHandle);
        }
        referenceMap.clear();
    }

    public int getReferenceCount() throws JavetV8RuntimeLockConflictException {
        checkLock();
        return referenceMap.size();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
