/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_caoccao_javet_interop_V8Native */

#ifndef _Included_com_caoccao_javet_interop_V8Native
#define _Included_com_caoccao_javet_interop_V8Native
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    add
 * Signature: (JJILjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_add
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    allowCodeGenerationFromStrings
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_allowCodeGenerationFromStrings
  (JNIEnv *, jobject, jlong, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    await
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_await
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    call
 * Signature: (JJILjava/lang/Object;Z[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_call
  (JNIEnv *, jobject, jlong, jlong, jint, jobject, jboolean, jobjectArray);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    callAsConstructor
 * Signature: (JJI[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_callAsConstructor
  (JNIEnv *, jobject, jlong, jlong, jint, jobjectArray);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    clearInternalStatistic
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearInternalStatistic
  (JNIEnv *, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    clearWeak
 * Signature: (JJI)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearWeak
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    cloneV8Value
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_cloneV8Value
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    closeV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    compile
 * Signature: (JLjava/lang/String;ZLjava/lang/String;IIIZZ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_compile
  (JNIEnv *, jobject, jlong, jstring, jboolean, jstring, jint, jint, jint, jboolean, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    contextGet
 * Signature: (JJII)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_contextGet
  (JNIEnv *, jobject, jlong, jlong, jint, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    contextGetLength
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_contextGetLength
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    contextIsContextType
 * Signature: (JJII)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_contextIsContextType
  (JNIEnv *, jobject, jlong, jlong, jint, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    contextSetLength
 * Signature: (JJII)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_contextSetLength
  (JNIEnv *, jobject, jlong, jlong, jint, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    createV8Inspector
 * Signature: (JLjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Inspector
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    createV8Runtime
 * Signature: (Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    createV8Value
 * Signature: (JILjava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Value
  (JNIEnv *, jobject, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    delete
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_delete
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    deletePrivateProperty
 * Signature: (JJILjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_deletePrivateProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    equals
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_equals
  (JNIEnv *, jobject, jlong, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    execute
 * Signature: (JLjava/lang/String;ZLjava/lang/String;IIIZZ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute
  (JNIEnv *, jobject, jlong, jstring, jboolean, jstring, jint, jint, jint, jboolean, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionCanDiscardCompiled
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionCanDiscardCompiled
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionCopyScopeInfoFrom
 * Signature: (JJIJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionCopyScopeInfoFrom
  (JNIEnv *, jobject, jlong, jlong, jint, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionDiscardCompiled
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionDiscardCompiled
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionGetContext
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetContext
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionGetScriptSource
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetScriptSource
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionGetSourceCode
 * Signature: (JJI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_functionGetSourceCode
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionIsCompiled
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionIsCompiled
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionSetContext
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetContext
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionSetScriptSource
 * Signature: (JJILjava/lang/Object;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetScriptSource
  (JNIEnv *, jobject, jlong, jlong, jint, jobject, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    functionSetSourceCode
 * Signature: (JJILjava/lang/String;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_functionSetSourceCode
  (JNIEnv *, jobject, jlong, jlong, jint, jstring, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    get
 * Signature: (JJILjava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_get
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getGlobalObject
 * Signature: (J)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getGlobalObject
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getIdentityHash
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getIdentityHash
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getInternalProperties
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getInternalProperties
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getInternalStatistic
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_com_caoccao_javet_interop_V8Native_getInternalStatistic
  (JNIEnv *, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getJSFunctionType
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getJSFunctionType
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getJSScopeType
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getJSScopeType
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getLength
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getLength
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getOwnPropertyNames
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getOwnPropertyNames
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getPrivateProperty
 * Signature: (JJILjava/lang/String;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPrivateProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getProperty
 * Signature: (JJILjava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getPropertyNames
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPropertyNames
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getPrototype
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPrototype
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getSize
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getSize
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getV8HeapSpaceStatistics
 * Signature: (JI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8HeapSpaceStatistics
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getV8HeapStatistics
 * Signature: (J)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8HeapStatistics
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getV8SharedMemoryStatistics
 * Signature: ()Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getV8SharedMemoryStatistics
  (JNIEnv *, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
  (JNIEnv *, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    has
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_has
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasInternalType
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasInternalType
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasOwnProperty
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasOwnProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasPendingException
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPendingException
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasPendingMessage
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPendingMessage
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasPrivateProperty
 * Signature: (JJILjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasPrivateProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasScheduledException
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasScheduledException
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    idleNotificationDeadline
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_idleNotificationDeadline
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    invoke
 * Signature: (JJILjava/lang/String;Z[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_invoke
  (JNIEnv *, jobject, jlong, jlong, jint, jstring, jboolean, jobjectArray);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    isDead
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isDead
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    isInUse
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isInUse
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    isWeak
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isWeak
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    lockV8Runtime
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    lowMemoryNotification
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lowMemoryNotification
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    moduleEvaluate
 * Signature: (JJIZ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleEvaluate
  (JNIEnv *, jobject, jlong, jlong, jint, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    moduleGetException
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetException
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    moduleGetNamespace
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetNamespace
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    moduleGetScriptId
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetScriptId
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    moduleGetStatus
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_moduleGetStatus
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    moduleInstantiate
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_moduleInstantiate
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseCatch
 * Signature: (JJIJ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseCatch
  (JNIEnv *, jobject, jlong, jlong, jint, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseGetPromise
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetPromise
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseGetResult
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetResult
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseGetState
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_promiseGetState
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseHasHandler
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseHasHandler
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseMarkAsHandled
 * Signature: (JJI)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_promiseMarkAsHandled
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseReject
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseReject
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseResolve
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promiseResolve
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promiseThen
 * Signature: (JJIJJ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_promiseThen
  (JNIEnv *, jobject, jlong, jlong, jint, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    promoteScheduledException
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_promoteScheduledException
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    proxyGetHandler
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyGetHandler
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    proxyGetTarget
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_proxyGetTarget
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    proxyIsRevoked
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_proxyIsRevoked
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    proxyRevoke
 * Signature: (JJI)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_proxyRevoke
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    registerGCEpilogueCallback
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerGCEpilogueCallback
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    registerGCPrologueCallback
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerGCPrologueCallback
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    registerV8Runtime
 * Signature: (JLjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_registerV8Runtime
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    removeJNIGlobalRef
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeJNIGlobalRef
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    removeReferenceHandle
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
  (JNIEnv *, jobject, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    reportPendingMessages
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_reportPendingMessages
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    requestGarbageCollectionForTesting
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
  (JNIEnv *, jobject, jlong, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    resetV8Context
 * Signature: (JLjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Context
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    resetV8Isolate
 * Signature: (JLjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Isolate
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    sameValue
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_sameValue
  (JNIEnv *, jobject, jlong, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    scriptRun
 * Signature: (JJIZ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_scriptRun
  (JNIEnv *, jobject, jlong, jlong, jint, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    set
 * Signature: (JJILjava/lang/Object;Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_set
  (JNIEnv *, jobject, jlong, jlong, jint, jobject, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setAccessor
 * Signature: (JJILjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setAccessor
  (JNIEnv *, jobject, jlong, jlong, jint, jobject, jobject, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setPrivateProperty
 * Signature: (JJILjava/lang/String;Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setPrivateProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jstring, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setProperty
 * Signature: (JJILjava/lang/Object;Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setProperty
  (JNIEnv *, jobject, jlong, jlong, jint, jobject, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setPrototype
 * Signature: (JJIJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setPrototype
  (JNIEnv *, jobject, jlong, jlong, jint, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setWeak
 * Signature: (JJILjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setWeak
  (JNIEnv *, jobject, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    strictEquals
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_strictEquals
  (JNIEnv *, jobject, jlong, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    terminateExecution
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_terminateExecution
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    toProtoString
 * Signature: (JJI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toProtoString
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    toString
 * Signature: (JJI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
  (JNIEnv *, jobject, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    unlockV8Runtime
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    unregisterGCEpilogueCallback
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unregisterGCEpilogueCallback
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    unregisterGCPrologueCallback
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unregisterGCPrologueCallback
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    v8InspectorSend
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_v8InspectorSend
  (JNIEnv *, jobject, jlong, jstring);

#ifdef __cplusplus
}
#endif
#endif
