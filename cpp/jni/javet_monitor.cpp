/*
 *   Copyright (c) 2021-2024. caoccao.com Sam Cao
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
        void Initialize(JNIEnv* jniEnv) noexcept {
            jclassV8HeapSpaceStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics");
            jmethodIDV8HeapSpaceStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapSpaceStatistics, "<init>", "(Ljava/lang/String;JJJJ)V");

            jclassV8HeapStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8HeapStatistics");
            jmethodIDV8HeapStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapStatistics, "<init>", "(JJJJJJJJJJJJJJ)V");

            jclassV8SharedMemoryStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8SharedMemoryStatistics");
            jmethodIDV8SharedMemoryStatisticsConstructor = jniEnv->GetMethodID(jclassV8SharedMemoryStatistics, "<init>", "(JJJ)V");
        }

        jobject GetHeapSpaceStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jint allocationSpaceIndex) noexcept {
            v8::HeapSpaceStatistics heapSpaceStatistics;
            v8Isolate->GetHeapSpaceStatistics(&heapSpaceStatistics, static_cast<size_t>(allocationSpaceIndex));
            return jniEnv->NewObject(jclassV8HeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsConstructor,
                Javet::Converter::ToJavaString(jniEnv, heapSpaceStatistics.space_name()),
                static_cast<jlong>(heapSpaceStatistics.physical_space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_available_size()),
                static_cast<jlong>(heapSpaceStatistics.space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_used_size()));
        }

        jobject GetHeapStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate) noexcept {
            v8::HeapStatistics heapStatistics;
            v8Isolate->GetHeapStatistics(&heapStatistics);
            return jniEnv->NewObject(jclassV8HeapStatistics, jmethodIDV8HeapStatisticsConstructor,
                static_cast<jlong>(heapStatistics.does_zap_garbage()),
                static_cast<jlong>(heapStatistics.external_memory()),
                static_cast<jlong>(heapStatistics.heap_size_limit()),
                static_cast<jlong>(heapStatistics.malloced_memory()),
                static_cast<jlong>(heapStatistics.number_of_detached_contexts()),
                static_cast<jlong>(heapStatistics.number_of_native_contexts()),
                static_cast<jlong>(heapStatistics.peak_malloced_memory()),
                static_cast<jlong>(heapStatistics.total_available_size()),
                static_cast<jlong>(heapStatistics.total_global_handles_size()),
                static_cast<jlong>(heapStatistics.total_heap_size()),
                static_cast<jlong>(heapStatistics.total_heap_size_executable()),
                static_cast<jlong>(heapStatistics.total_physical_size()),
                static_cast<jlong>(heapStatistics.used_global_handles_size()),
                static_cast<jlong>(heapStatistics.used_heap_size()));
        }

        jobject GetV8SharedMemoryStatistics(JNIEnv* jniEnv) noexcept {
            v8::SharedMemoryStatistics sharedMemoryStatistics;
            v8::V8::GetSharedMemoryStatistics(&sharedMemoryStatistics);
            return jniEnv->NewObject(jclassV8SharedMemoryStatistics, jmethodIDV8SharedMemoryStatisticsConstructor,
                static_cast<jlong>(sharedMemoryStatistics.read_only_space_physical_size()),
                static_cast<jlong>(sharedMemoryStatistics.read_only_space_size()),
                static_cast<jlong>(sharedMemoryStatistics.read_only_space_used_size()));
        }

#ifdef ENABLE_MONITOR
        JavetNativeMonitor::JavetNativeMonitor() noexcept {
            Clear();
        }

        jlongArray JavetNativeMonitor::GetCounters(JNIEnv* jniEnv) noexcept {
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

