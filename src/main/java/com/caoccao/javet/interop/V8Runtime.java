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

import com.caoccao.javet.exceptions.*;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLoggable;
import com.caoccao.javet.interfaces.IJavetResettable;
import com.caoccao.javet.utils.JavetConverterUtils;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.reference.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public final class V8Runtime implements
        IJavetClosable, IJavetResettable, IJavetLoggable, IV8Executable, IV8Creatable {
    private static final long INVALID_THREAD_ID = -1L;

    private Map<Long, V8CallbackContext> callbackContextMap;
    private JavetConverterUtils converter;
    private String globalName;
    private long handle;
    private Logger logger;
    private Map<Long, IV8ValueReference> referenceMap;
    private long threadId;
    private V8Host v8Host;

    V8Runtime(V8Host v8Host, long handle, String globalName) {
        assert handle != 0;
        callbackContextMap = new TreeMap<>();
        converter = new JavetConverterUtils();
        this.globalName = globalName;
        this.handle = handle;
        logger = Logger.getLogger(getClass().getName());
        referenceMap = new TreeMap<>();
        threadId = INVALID_THREAD_ID;
        this.v8Host = v8Host;
    }

    public void add(IV8ValueSet iV8ValueKeySet, V8Value value) throws JavetException {
        checkLock();
        decorateV8Value(value);
        V8Native.add(handle, iV8ValueKeySet.getHandle(), iV8ValueKeySet.getType(), value);
    }

    public void addReference(IV8ValueReference iV8ValueReference) throws
            JavetV8RuntimeAlreadyClosedException, JavetV8RuntimeLockConflictException {
        checkLock();
        referenceMap.put(iV8ValueReference.getHandle(), iV8ValueReference);
    }

    public <T extends V8Value> T call(
            IV8ValueObject iV8ValueObject, IV8ValueObject receiver, boolean returnResult, V8Value... v8Values) throws
            JavetV8RuntimeAlreadyClosedException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyRegisteredException {
        checkLock();
        decorateV8Values(v8Values);
        return decorateV8Value((T) V8Native.call(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), receiver, returnResult, v8Values));
    }

    public void checkLock() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        if (handle == 0L) {
            throw new JavetV8RuntimeAlreadyClosedException();
        }
        final long currentThreadId = Thread.currentThread().getId();
        if (threadId != currentThreadId) {
            throw new JavetV8RuntimeLockConflictException(threadId, currentThreadId);
        }
    }

    @Override
    public void close() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        removeCallbacks();
        removeReferences();
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
    public void compileOnly(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        checkLock();
        V8Native.compileOnly(
                handle, scriptString, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
    }

    @Override
    public V8CallbackContext createCallback(
            IV8ValueObject iV8ValueObject, String functionName,
            IV8CallbackReceiver callbackReceiver, Method callbackMethod) throws JavetException {
        checkLock();
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                this, functionName, callbackReceiver, callbackMethod);
        long v8CallbackHandle = V8Native.createCallback(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), v8CallbackContext);
        if (v8CallbackHandle == 0L) {
            throw new JavetV8CallbackNotRegisteredException();
        }
        v8CallbackContext.setHandle(v8CallbackHandle);
        callbackContextMap.put(v8CallbackContext.getHandle(), v8CallbackContext);
        return v8CallbackContext;
    }

    @Override
    public V8ValueArray createV8ValueArray() throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueArray) V8Native.createV8Value(handle, V8ValueReferenceType.Array));
    }

    @Override
    public V8ValueMap createV8ValueMap() throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueMap) V8Native.createV8Value(handle, V8ValueReferenceType.Map));
    }

    @Override
    public V8ValueObject createV8ValueObject() throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueObject) V8Native.createV8Value(handle, V8ValueReferenceType.Object));
    }

    @Override
    public V8ValueSet createV8ValueSet() throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueSet) V8Native.createV8Value(handle, V8ValueReferenceType.Set));
    }

    public <T extends V8Value> T decorateV8Value(T v8Value) throws
            JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyRegisteredException,
            JavetV8RuntimeAlreadyClosedException {
        if (v8Value != null) {
            if (v8Value.getV8Runtime() == null) {
                v8Value.setV8Runtime(this);
            } else if (v8Value.getV8Runtime() != this) {
                throw new JavetV8RuntimeAlreadyRegisteredException();
            }
        }
        return v8Value;
    }

    public <T extends V8Value> int decorateV8Values(T... v8Values) throws
            JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyRegisteredException,
            JavetV8RuntimeAlreadyClosedException {
        if (v8Values != null && v8Values.length > 0) {
            for (T v8Value : v8Values) {
                decorateV8Value(v8Value);
            }
            return v8Values.length;
        }
        return 0;
    }

    public boolean delete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        checkLock();
        decorateV8Value(key);
        return V8Native.delete(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key);
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

    public <T extends V8Value> T get(
            IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        checkLock();
        return decorateV8Value((T) V8Native.get(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key));
    }

    public int getCallbackContextCount()
            throws JavetV8RuntimeAlreadyClosedException, JavetV8RuntimeLockConflictException {
        checkLock();
        return callbackContextMap.size();
    }

    public JavetConverterUtils getConverter() {
        return converter;
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public V8ValueGlobalObject getGlobalObject() throws
            JavetV8RuntimeAlreadyClosedException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyRegisteredException {
        return decorateV8Value((V8ValueGlobalObject) V8Native.getGlobalObject(handle));
    }

    public long getHandle() {
        return handle;
    }

    public int getLength(IV8ValueArray iV8ValueArray) throws JavetException {
        checkLock();
        return V8Native.getLength(handle, iV8ValueArray.getHandle(), iV8ValueArray.getType());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public IV8ValueArray getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueArray) V8Native.getOwnPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType()));
    }

    public <T extends V8Value> T getProperty(
            IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        checkLock();
        decorateV8Value(key);
        return decorateV8Value((T) V8Native.getProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key));
    }

    public IV8ValueArray getPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        checkLock();
        return decorateV8Value((V8ValueArray) V8Native.getPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType()));
    }

    public int getReferenceCount()
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        checkLock();
        return referenceMap.size();
    }

    public int getSize(IV8ValueKeyContainer iV8ValueKeyContainer) throws JavetException {
        checkLock();
        return V8Native.getSize(handle, iV8ValueKeyContainer.getHandle(), iV8ValueKeyContainer.getType());
    }

    public boolean hasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        checkLock();
        decorateV8Value(key);
        return V8Native.hasOwnProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key);
    }

    public boolean has(IV8ValueKeyContainer iV8ValueKeyContainer, V8Value value) throws JavetException {
        checkLock();
        decorateV8Value(value);
        return V8Native.has(handle, iV8ValueKeyContainer.getHandle(), iV8ValueKeyContainer.getType(), value);
    }

    public <T extends V8Value> T invoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values) throws
            JavetV8RuntimeAlreadyClosedException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyRegisteredException {
        checkLock();
        decorateV8Values(v8Values);
        return decorateV8Value((T) V8Native.invoke(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), functionName, returnResult, v8Values));
    }

    public boolean isLocked() {
        return threadId != INVALID_THREAD_ID;
    }

    public void lock() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        if (!isLocked()) {
            V8Native.lockV8Runtime(handle);
            threadId = Thread.currentThread().getId();
        } else {
            checkLock();
        }
    }

    public V8Value receiveCallback(long v8CallbackContextHandle, V8ValueArray args) throws JavetException {
        V8Value[] values = null;
        try {
            if (args != null) {
                decorateV8Value(args);
            }
            V8CallbackContext v8CallbackContext = callbackContextMap.get(v8CallbackContextHandle);
            if (v8CallbackContext == null) {
                logWarn(
                        "V8 callback context is not found by handle {0}.",
                        Long.toString(v8CallbackContextHandle));
            } else {
                Method method = v8CallbackContext.getCallbackMethod();
                IV8CallbackReceiver callbackReceiver = v8CallbackContext.getCallbackReceiver();
                Object result = null;
                if (args == null || args.getLength() == 0) {
                    result = method.invoke(callbackReceiver);
                } else {
                    final int length = args.getLength();
                    values = new V8Value[length];
                    for (int i = 0; i < length; ++i) {
                        values[i] = args.get(i);
                    }
                    if (method.isVarArgs()) {
                        result = method.invoke(callbackReceiver, new Object[]{values});
                    } else {
                        Object[] objectValues = new Object[length];
                        System.arraycopy(values, 0, objectValues, 0, length);
                        result = method.invoke(callbackReceiver, objectValues);
                    }
                }
                if (result != null) {
                    if (result instanceof V8Value) {
                        decorateV8Value((V8Value) result);
                    } else {
                        result = converter.toV8Value(this, result);
                    }
                } else {
                    result = converter.toV8Value(this, null);
                }
                if (v8CallbackContext.isReturnResult()) {
                    return (V8Value) result;
                } else {
                    JavetResourceUtils.safeClose(result);
                }
            }
        } catch (Throwable t) {
            logWarn(t.getMessage());
        } finally {
            JavetResourceUtils.safeClose(args);
            JavetResourceUtils.safeClose(values);
        }
        return decorateV8Value(new V8ValueUndefined());
    }

    public void removeCallback(V8CallbackContext v8CallbackContext)
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        checkLock();
        final long v8CallbackHandle = v8CallbackContext.getHandle();
        if (callbackContextMap.containsKey(v8CallbackHandle)) {
            V8Native.removeCallbackHandle(v8CallbackHandle);
            callbackContextMap.remove(v8CallbackHandle);
        }
    }

    private void removeCallbacks()
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        if (!callbackContextMap.isEmpty()) {
            logWarn("{0} V8 callback(s) not recycled.", callbackContextMap.size());
            if (!isLocked()) {
                lock();
            }
            for (V8CallbackContext v8CallbackContext : callbackContextMap.values()) {
                removeCallback(v8CallbackContext);
            }
            callbackContextMap.clear();
        }
    }

    public void removeReference(IV8ValueReference iV8ValueReference)
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        checkLock();
        final long referenceHandle = iV8ValueReference.getHandle();
        if (referenceMap.containsKey(referenceHandle)) {
            V8Native.removeReferenceHandle(referenceHandle);
            referenceMap.remove(referenceHandle);
        }
    }

    private void removeReferences() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        if (!referenceMap.isEmpty()) {
            logWarn("{0} V8 object(s) not recycled.", referenceMap.size());
            if (!isLocked()) {
                lock();
            }
            for (IV8ValueReference iV8ValueReference : referenceMap.values()) {
                removeReference(iV8ValueReference);
            }
            referenceMap.clear();
        }
    }

    @Override
    public void reset() throws JavetV8RuntimeAlreadyClosedException, JavetV8RuntimeLockConflictException {
        removeReferences();
        if (isLocked()) {
            try {
                unlock();
            } catch (JavetV8RuntimeLockConflictException e) {
            }
        }
        V8Native.resetV8Runtime(handle, globalName);
    }

    public boolean set(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        checkLock();
        decorateV8Values(key, value);
        return V8Native.set(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key, value);
    }

    public boolean setProperty(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        checkLock();
        decorateV8Values(key, value);
        return V8Native.setProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key, value);
    }

    public String toProtoString(IV8ValueReference iV8ValueReference)
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        checkLock();
        return V8Native.toProtoString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType());
    }

    public String toString(IV8ValueReference iV8ValueReference)
            throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        checkLock();
        return V8Native.toString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType());
    }

    public void unlock() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        checkLock();
        threadId = INVALID_THREAD_ID;
        V8Native.unlockV8Runtime(handle);
    }
}
