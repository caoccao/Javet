#include <jni.h>
#include <libplatform/libplatform.h>
#include <iostream>
#include <v8.h>
#include <v8-inspector.h>
#include <functional>
#include <string.h>
#include <map>
#include <cstdlib>
#include "com_caoccao_javet_interop_V8Native.h"
#include "constants.h"
#include "globals.h"

JNIEXPORT jint JNICALL JNI_OnLoad
(JavaVM* vm, void*) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_8) != JNI_OK) {
        return ERROR_JNI_ON_LOAD;
    }
    if (env == nullptr) {
        return ERROR_JNI_ON_LOAD;
    }

    v8::V8::InitializeICU();
    GlobalV8Platform = v8::platform::NewDefaultPlatform();
    v8::V8::InitializePlatform(GlobalV8Platform.get());
    v8::V8::Initialize();

    return JNI_VERSION_1_8;
}

JNIEXPORT jstring JNICALL Java_com_caoccao_javet_interop_V8Native_getVersion
(JNIEnv* env, jclass) {
    const char* utfString = v8::V8::GetVersion();
    return env->NewStringUTF(utfString);
}
