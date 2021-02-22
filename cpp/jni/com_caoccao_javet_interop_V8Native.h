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
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    call
 * Signature: (JJILjava/lang/Object;Z[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_call
  (JNIEnv *, jclass, jlong, jlong, jint, jobject, jboolean, jobjectArray);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    clearWeak
 * Signature: (JJI)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_clearWeak
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    cloneV8Value
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_cloneV8Value
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    closeV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    compileOnly
 * Signature: (JLjava/lang/String;Ljava/lang/String;IIIZZ)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_compileOnly
  (JNIEnv *, jclass, jlong, jstring, jstring, jint, jint, jint, jboolean, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    createV8Runtime
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    createV8Value
 * Signature: (JILjava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Value
  (JNIEnv *, jclass, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    delete
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_delete
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    equals
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_equals
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    execute
 * Signature: (JLjava/lang/String;ZLjava/lang/String;IIIZZ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute
  (JNIEnv *, jclass, jlong, jstring, jboolean, jstring, jint, jint, jint, jboolean, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    get
 * Signature: (JJILjava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_get
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getGlobalObject
 * Signature: (J)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getGlobalObject
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getLength
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getLength
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getSize
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getSize
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getOwnPropertyNames
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getOwnPropertyNames
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getPropertyNames
 * Signature: (JJI)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getPropertyNames
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getProperty
 * Signature: (JJILjava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_getProperty
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
  (JNIEnv *, jclass);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    has
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_has
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    hasOwnProperty
 * Signature: (JJILjava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_hasOwnProperty
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    invoke
 * Signature: (JJILjava/lang/String;Z[Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_invoke
  (JNIEnv *, jclass, jlong, jlong, jint, jstring, jboolean, jobjectArray);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    isWeak
 * Signature: (JJI)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_isWeak
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    lockV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    removeJNIGlobalRef
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeJNIGlobalRef
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    removeReferenceHandle
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_removeReferenceHandle
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    requestGarbageCollectionForTesting
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_requestGarbageCollectionForTesting
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    resetV8Context
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Context
  (JNIEnv *, jclass, jlong, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    resetV8Isolate
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Isolate
  (JNIEnv *, jclass, jlong, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    set
 * Signature: (JJILjava/lang/Object;Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_set
  (JNIEnv *, jclass, jlong, jlong, jint, jobject, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setFlags
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setFlags
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setProperty
 * Signature: (JJILjava/lang/Object;Ljava/lang/Object;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_setProperty
  (JNIEnv *, jclass, jlong, jlong, jint, jobject, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setWeak
 * Signature: (JJILjava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setWeak
  (JNIEnv *, jclass, jlong, jlong, jint, jobject);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    sameValue
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_sameValue
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    strictEquals
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_caoccao_javet_interop_V8Native_strictEquals
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    toProtoString
 * Signature: (JJI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toProtoString
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    toString
 * Signature: (JJI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_toString
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    unlockV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
