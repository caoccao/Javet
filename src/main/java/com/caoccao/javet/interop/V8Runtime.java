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
import com.caoccao.javet.exceptions.JavetV8LockConflictException;
import com.caoccao.javet.exceptions.JavetV8RuntimeAlreadyClosedException;
import com.caoccao.javet.exceptions.JavetV8RuntimeAlreadyRegisteredException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.interop.executors.V8PathExecutor;
import com.caoccao.javet.interop.executors.V8StringExecutor;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.reference.*;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public final class V8Runtime implements IJavetClosable, IV8Executable, IV8Creatable {
    private static final long INVALID_HANDLE = 0L;
    private static final String PROPERTY_DATA_VIEW = "DataView";
    private static final String DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR = "Javet Inspector {0}";

    private String globalName;
    private long handle;
    private IJavetLogger logger;
    private boolean pooled;
    private Map<Long, IV8ValueReference> referenceMap;
    private V8Host v8Host;
    private V8Inspector v8Inspector;

    V8Runtime(V8Host v8Host, long handle, boolean pooled, String globalName) {
        assert handle != 0;
        this.globalName = globalName;
        this.handle = handle;
        logger = new JavetDefaultLogger(getClass().getName());
        this.pooled = pooled;
        referenceMap = new TreeMap<>();
        this.v8Host = v8Host;
        v8Inspector = null;
    }

    public void add(IV8ValueSet iV8ValueKeySet, V8Value value) throws JavetException {
        decorateV8Value(value);
        V8Native.add(handle, iV8ValueKeySet.getHandle(), iV8ValueKeySet.getType(), value);
    }

    public void addReference(IV8ValueReference iV8ValueReference) {
        referenceMap.put(iV8ValueReference.getHandle(), iV8ValueReference);
    }

    public <T extends V8Value> T call(
            IV8ValueObject iV8ValueObject, IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) V8Native.call(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), receiver, returnResult, v8Values));
    }

    public <T extends V8Value> T callAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) V8Native.callAsConstructor(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), v8Values));
    }

    public void clearWeak(IV8ValueReference iV8ValueReference) throws JavetException {
        V8Native.clearWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType());
    }

    public <T extends V8Value> T cloneV8Value(IV8ValueReference iV8ValueReference) throws JavetException {
        return decorateV8Value((T) V8Native.cloneV8Value(
                handle, iV8ValueReference.getHandle(), iV8ValueReference.getType()));
    }

    @Override
    public void close() throws JavetException {
        if (pooled) {
            close(false);
        } else {
            close(true);
        }
    }

    public void close(boolean forceClose) throws JavetException {
        if (handle != INVALID_HANDLE && forceClose) {
            removeReferences();
            v8Host.closeV8Runtime(this);
            v8Inspector = null;
            handle = INVALID_HANDLE;
        }
    }

    @Override
    public void compileOnly(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        V8Native.compileOnly(
                handle, scriptString, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
    }

    @Override
    public V8ValueArray createV8ValueArray() throws JavetException {
        return decorateV8Value((V8ValueArray) V8Native.createV8Value(
                handle, V8ValueReferenceType.Array, null));
    }

    @Override
    public V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException {
        return decorateV8Value((V8ValueArrayBuffer) V8Native.createV8Value(
                handle, V8ValueReferenceType.ArrayBuffer, new V8ValueInteger(length)));
    }

    @Override
    public V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException {
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(PROPERTY_DATA_VIEW)) {
            return v8ValueFunction.callAsConstructor(v8ValueArrayBuffer);
        }
    }

    @Override
    public V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException {
        V8ValueFunction v8ValueFunction = decorateV8Value((V8ValueFunction) V8Native.createV8Value(
                handle, V8ValueReferenceType.Function, javetCallbackContext));
        v8ValueFunction.setV8CallbackContext(javetCallbackContext);
        return v8ValueFunction;
    }

    @Override
    public V8ValueMap createV8ValueMap() throws JavetException {
        return decorateV8Value((V8ValueMap) V8Native.createV8Value(handle, V8ValueReferenceType.Map, null));
    }

    @Override
    public V8ValueNull createV8ValueNull() {
        V8ValueNull v8ValueNull = new V8ValueNull();
        try {
            v8ValueNull.setV8Runtime(this);
        } catch (JavetException javetException) {
        }
        return v8ValueNull;
    }

    @Override
    public V8ValueObject createV8ValueObject() throws JavetException {
        return decorateV8Value((V8ValueObject) V8Native.createV8Value(handle, V8ValueReferenceType.Object, null));
    }

    @Override
    public V8ValueSet createV8ValueSet() throws JavetException {
        return decorateV8Value((V8ValueSet) V8Native.createV8Value(handle, V8ValueReferenceType.Set, null));
    }

    @Override
    public V8ValueTypedArray createV8ValueTypedArray(int type, int length) throws JavetException {
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(V8ValueTypedArray.getName(type))) {
            return v8ValueFunction.callAsConstructor(new V8ValueInteger(length));
        }
    }

    @Override
    public V8ValueUndefined createV8ValueUndefined() {
        V8ValueUndefined v8ValueUndefined = new V8ValueUndefined();
        try {
            v8ValueUndefined.setV8Runtime(this);
        } catch (JavetException javetException) {
        }
        return v8ValueUndefined;
    }

    public <T extends V8Value> T decorateV8Value(T v8Value) throws JavetException {
        if (v8Value != null) {
            if (v8Value.getV8Runtime() == null) {
                v8Value.setV8Runtime(this);
            } else if (v8Value.getV8Runtime() != this) {
                throw new JavetV8RuntimeAlreadyRegisteredException();
            }
        }
        return v8Value;
    }

    public <T extends V8Value> int decorateV8Values(T... v8Values) throws JavetException {
        if (v8Values != null && v8Values.length > 0) {
            for (T v8Value : v8Values) {
                decorateV8Value(v8Value);
            }
            return v8Values.length;
        }
        return 0;
    }

    public boolean delete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return V8Native.delete(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key);
    }

    public boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return V8Native.equals(handle, iV8ValueReference1.getHandle(), iV8ValueReference2.getHandle());
    }

    @Override
    public <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) V8Native.execute(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
    }

    public <T extends V8Value> T get(
            IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return decorateV8Value((T) V8Native.get(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key));
    }

    @Override
    public IV8Executor getExecutor(Path scriptPath) {
        return new V8PathExecutor(this, scriptPath);
    }

    @Override
    public IV8Executor getExecutor(String scriptString) {
        return new V8StringExecutor(this, scriptString);
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public int getIdentityHash(IV8ValueObject iV8ValueObject) throws JavetException {
        return V8Native.getIdentityHash(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType());
    }

    public IJavetLogger getLogger() {
        return logger;
    }

    public void setLogger(IJavetLogger logger) {
        this.logger = logger;
    }

    public V8ValueGlobalObject getGlobalObject() throws JavetException {
        return decorateV8Value((V8ValueGlobalObject) V8Native.getGlobalObject(handle));
    }

    public long getHandle() {
        return handle;
    }

    public int getLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return V8Native.getLength(handle, iV8ValueArray.getHandle(), iV8ValueArray.getType());
    }

    public int getLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return V8Native.getLength(handle, iV8ValueTypedArray.getHandle(), iV8ValueTypedArray.getType());
    }

    public IV8ValueArray getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueArray) V8Native.getOwnPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType()));
    }

    public <T extends V8Value> T getProperty(
            IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return decorateV8Value((T) V8Native.getProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key));
    }

    public IV8ValueArray getPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueArray) V8Native.getPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType()));
    }

    public int getReferenceCount() {
        return referenceMap.size();
    }

    public int getSize(IV8ValueKeyContainer iV8ValueKeyContainer) throws JavetException {
        return V8Native.getSize(handle, iV8ValueKeyContainer.getHandle(), iV8ValueKeyContainer.getType());
    }

    public V8Inspector getV8Inspector() {
        return getV8Inspector(MessageFormat.format(DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR, Long.toString(handle)));
    }

    public V8Inspector getV8Inspector(String name) {
        if (v8Inspector == null) {
            v8Inspector = new V8Inspector(this, name);
        }
        return v8Inspector;
    }

    public V8Locker getV8Locker() throws JavetV8LockConflictException {
        return new V8Locker(this);
    }

    public boolean has(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        decorateV8Value(value);
        return V8Native.has(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), value);
    }

    public boolean hasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return V8Native.hasOwnProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key);
    }

    public <T extends V8Value> T invoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) V8Native.invoke(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), functionName, returnResult, v8Values));
    }

    public boolean isDead() {
        return V8Native.isDead(handle);
    }

    public boolean isInUse() {
        return V8Native.isInUse(handle);
    }

    public boolean isPooled() {
        return pooled;
    }

    public boolean isWeak(IV8ValueReference iV8ValueReference) {
        return V8Native.isWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType());
    }

    public void removeJNIGlobalRef(long handle) {
        if (handle != INVALID_HANDLE) {
            V8Native.removeJNIGlobalRef(handle);
        }
    }

    public void removeReference(IV8ValueReference iV8ValueReference) {
        final long referenceHandle = iV8ValueReference.getHandle();
        if (referenceMap.containsKey(referenceHandle)) {
            V8Native.removeReferenceHandle(referenceHandle);
            referenceMap.remove(referenceHandle);
        }
    }

    private void removeReferences() throws JavetException {
        if (!referenceMap.isEmpty()) {
            final int referenceCount = referenceMap.size();
            int weakReferenceCount = 0;
            for (IV8ValueReference iV8ValueReference : new ArrayList<>(referenceMap.values())) {
                if (iV8ValueReference.isWeak()) {
                    ++weakReferenceCount;
                }
                removeReference(iV8ValueReference);
            }
            if (weakReferenceCount < referenceCount) {
                logger.logWarn("{0} V8 object(s) not recycled, {1} weak.", referenceCount, weakReferenceCount);
            } else {
                logger.logDebug("{0} V8 object(s) not recycled, {1} weak.", referenceCount, weakReferenceCount);
            }
            referenceMap.clear();
        }
    }

    /**
     * Requests GC for testing.
     * Note: --expose_gc must be set.
     *
     * @param fullGC true = Full GC, false = Minor GC
     */
    public void requestGarbageCollectionForTesting(boolean fullGC) {
        V8Native.requestGarbageCollectionForTesting(handle, fullGC);
    }

    /**
     * Reset V8 context.
     * This is a light-weight and recommended reset.
     *
     * @return the self
     * @throws JavetException the javet exception
     */
    public V8Runtime resetContext() throws JavetException {
        removeReferences();
        v8Inspector = null;
        V8Native.resetV8Context(handle, globalName);
        return this;
    }

    /**
     * Reset V8 isolate.
     * This is a heavy reset. Please avoid using it in performance sensitive scenario.
     *
     * @return the self
     * @throws JavetException the javet exception
     */
    public V8Runtime resetIsolate() throws JavetException {
        removeReferences();
        v8Inspector = null;
        V8Native.resetV8Isolate(handle, globalName);
        return this;
    }

    public boolean set(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        decorateV8Values(key, value);
        return V8Native.set(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key, value);
    }

    public boolean setProperty(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        decorateV8Values(key, value);
        return V8Native.setProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType(), key, value);
    }

    public void setWeak(IV8ValueReference iV8ValueReference) {
        V8Native.setWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType(), iV8ValueReference);
    }

    public boolean sameValue(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2) {
        return V8Native.sameValue(handle, iV8ValueReference1.getHandle(), iV8ValueReference2.getHandle());
    }

    public boolean strictEquals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2) {
        return V8Native.strictEquals(handle, iV8ValueReference1.getHandle(), iV8ValueReference2.getHandle());
    }

    /**
     * Terminate execution.
     * <p>
     * Forcefully terminate the current thread of JavaScript execution
     * in the given isolate.
     * <p>
     * This method can be used by any thread even if that thread has not
     * acquired the V8 lock with a Locker object.
     */
    public void terminateExecution() {
        V8Native.terminateExecution(handle);
    }

    public String toProtoString(IV8ValueReference iV8ValueReference) throws JavetV8RuntimeAlreadyClosedException {
        return V8Native.toProtoString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType());
    }

    public String toString(IV8ValueReference iV8ValueReference) throws JavetV8RuntimeAlreadyClosedException {
        return V8Native.toString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType());
    }
}
