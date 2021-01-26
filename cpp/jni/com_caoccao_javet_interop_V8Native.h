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
 * Method:    closeV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_closeV8Runtime
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    createV8Runtime
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_caoccao_javet_interop_V8Native_createV8Runtime
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    execute
 * Signature: (JLjava/lang/String;ZLjava/lang/String;IIIZZ)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_caoccao_javet_interop_V8Native_execute
  (JNIEnv *, jclass, jlong, jstring, jboolean, jstring, jint, jint, jint, jboolean, jboolean);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getLength
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_com_caoccao_javet_interop_V8Native_getLength
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
  (JNIEnv *, jclass);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    lockV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_lockV8Runtime
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
 * Method:    resetV8Runtime
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_resetV8Runtime
  (JNIEnv *, jclass, jlong, jstring);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    unlockV8Runtime
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_unlockV8Runtime
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_caoccao_javet_interop_V8Native
 * Method:    setFlags
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_caoccao_javet_interop_V8Native_setFlags
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
