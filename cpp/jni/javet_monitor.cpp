/*
 *   Copyright (c) 2021 caoccao.com Sam Cao
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

#include "javet_converter.h"
#include "javet_monitor.h"
#include "javet_logging.h"

namespace Javet {
    namespace Monitor {
        void Initialize(JNIEnv* jniEnv) {
            jclassV8HeapSpaceStatistics = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics"));
            jmethodIDV8HeapSpaceStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapSpaceStatistics, "<init>", "(Ljava/lang/String;IIII)V");

            jclassV8HeapStatistics = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/monitoring/V8HeapStatistics"));
            jmethodIDV8HeapStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapStatistics, "<init>", "(IIIIIIIIIIIIII)V");

            jclassV8SharedMemoryStatistics = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/interop/monitoring/V8SharedMemoryStatistics"));
            jmethodIDV8SharedMemoryStatisticsConstructor = jniEnv->GetMethodID(jclassV8SharedMemoryStatistics, "<init>", "(III)V");
        }

        jobject GetHeapSpaceStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate, jint allocationSpaceIndex) {
            v8::HeapSpaceStatistics heapSpaceStatistics;
            v8Isolate->GetHeapSpaceStatistics(&heapSpaceStatistics, static_cast<size_t>(allocationSpaceIndex));
            return jniEnv->NewObject(jclassV8HeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsConstructor,
                Javet::Converter::ToJavaString(jniEnv, heapSpaceStatistics.space_name()),
                static_cast<jint>(heapSpaceStatistics.physical_space_size()),
                static_cast<jint>(heapSpaceStatistics.space_available_size()),
                static_cast<jint>(heapSpaceStatistics.space_size()),
                static_cast<jint>(heapSpaceStatistics.space_used_size()));
        }

        jobject GetHeapStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate) {
            v8::HeapStatistics heapStatistics;
            v8Isolate->GetHeapStatistics(&heapStatistics);
            return jniEnv->NewObject(jclassV8HeapStatistics, jmethodIDV8HeapStatisticsConstructor,
                static_cast<jint>(heapStatistics.does_zap_garbage()),
                static_cast<jint>(heapStatistics.external_memory()),
                static_cast<jint>(heapStatistics.heap_size_limit()),
                static_cast<jint>(heapStatistics.malloced_memory()),
                static_cast<jint>(heapStatistics.number_of_detached_contexts()),
                static_cast<jint>(heapStatistics.number_of_native_contexts()),
                static_cast<jint>(heapStatistics.peak_malloced_memory()),
                static_cast<jint>(heapStatistics.total_available_size()),
                static_cast<jint>(heapStatistics.total_global_handles_size()),
                static_cast<jint>(heapStatistics.total_heap_size()),
                static_cast<jint>(heapStatistics.total_heap_size_executable()),
                static_cast<jint>(heapStatistics.total_physical_size()),
                static_cast<jint>(heapStatistics.used_global_handles_size()),
                static_cast<jint>(heapStatistics.used_heap_size()));
        }

        jobject GetV8SharedMemoryStatistics(JNIEnv* jniEnv) {
            v8::SharedMemoryStatistics sharedMemoryStatistics;
            v8::V8::GetSharedMemoryStatistics(&sharedMemoryStatistics);
            return jniEnv->NewObject(jclassV8SharedMemoryStatistics, jmethodIDV8SharedMemoryStatisticsConstructor,
                static_cast<jint>(sharedMemoryStatistics.read_only_space_physical_size()),
                static_cast<jint>(sharedMemoryStatistics.read_only_space_size()),
                static_cast<jint>(sharedMemoryStatistics.read_only_space_used_size()));
        }

#ifdef ENABLE_MONITOR
        JavetNativeMonitor::JavetNativeMonitor() {
            Clear();
        }

        jlongArray JavetNativeMonitor::GetCounters(JNIEnv* jniEnv) {
            jlong buffer[CounterType::Max];
            for (int i = 0; i < CounterType::Max; ++i) {
                buffer[i] = counters[i].load();
            }
            jlongArray returnDataArray = jniEnv->NewLongArray(CounterType::Max);
            jniEnv->SetLongArrayRegion(returnDataArray, 0, CounterType::Max, buffer);
            return returnDataArray;
        }
#endif

    }
}

#ifdef ENABLE_MONITOR
Javet::Monitor::JavetNativeMonitor GlobalJavetNativeMonitor;
#endif

