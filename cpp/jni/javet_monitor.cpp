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

#include "javet_monitor.h"
#include "javet_logging.h"

namespace Javet {
    namespace Monitor {
        jintArray GetHeapSpaceStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate, jint allocationSpace) {
            v8::HeapSpaceStatistics heapSpaceStatistics;
            v8Isolate->GetHeapSpaceStatistics(&heapSpaceStatistics, static_cast<size_t>(allocationSpace));
            jintArray intArray = jniEnv->NewIntArray(4);
            jboolean copy = false;
            jint* intArrayPointer = jniEnv->GetIntArrayElements(intArray, &copy);
            intArrayPointer[0] = static_cast<jint>(heapSpaceStatistics.physical_space_size());
            intArrayPointer[1] = static_cast<jint>(heapSpaceStatistics.space_available_size());
            intArrayPointer[2] = static_cast<jint>(heapSpaceStatistics.space_size());
            intArrayPointer[3] = static_cast<jint>(heapSpaceStatistics.space_used_size());
            jniEnv->ReleaseIntArrayElements(intArray, intArrayPointer, 0);
            return intArray;
        }

        jintArray GetHeapStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate) {
            v8::HeapStatistics heapStatistics;
            v8Isolate->GetHeapStatistics(&heapStatistics);
            jintArray intArray = jniEnv->NewIntArray(14);
            jboolean copy = false;
            jint* intArrayPointer = jniEnv->GetIntArrayElements(intArray, &copy);
            intArrayPointer[0] = static_cast<jint>(heapStatistics.does_zap_garbage());
            intArrayPointer[1] = static_cast<jint>(heapStatistics.external_memory());
            intArrayPointer[2] = static_cast<jint>(heapStatistics.heap_size_limit());
            intArrayPointer[3] = static_cast<jint>(heapStatistics.malloced_memory());
            intArrayPointer[4] = static_cast<jint>(heapStatistics.number_of_detached_contexts());
            intArrayPointer[5] = static_cast<jint>(heapStatistics.number_of_native_contexts());
            intArrayPointer[6] = static_cast<jint>(heapStatistics.peak_malloced_memory());
            intArrayPointer[7] = static_cast<jint>(heapStatistics.total_available_size());
            intArrayPointer[8] = static_cast<jint>(heapStatistics.total_global_handles_size());
            intArrayPointer[9] = static_cast<jint>(heapStatistics.total_heap_size());
            intArrayPointer[10] = static_cast<jint>(heapStatistics.total_heap_size_executable());
            intArrayPointer[11] = static_cast<jint>(heapStatistics.total_physical_size());
            intArrayPointer[12] = static_cast<jint>(heapStatistics.used_global_handles_size());
            intArrayPointer[13] = static_cast<jint>(heapStatistics.used_heap_size());
            jniEnv->ReleaseIntArrayElements(intArray, intArrayPointer, 0);
            return intArray;
        }

        jintArray GetV8SharedMemoryStatistics(JNIEnv* jniEnv) {
            v8::SharedMemoryStatistics sharedMemoryStatistics;
            v8::V8::GetSharedMemoryStatistics(&sharedMemoryStatistics);
            jintArray intArray = jniEnv->NewIntArray(3);
            jboolean copy = false;
            jint* intArrayPointer = jniEnv->GetIntArrayElements(intArray, &copy);
            intArrayPointer[0] = static_cast<jint>(sharedMemoryStatistics.read_only_space_physical_size());
            intArrayPointer[1] = static_cast<jint>(sharedMemoryStatistics.read_only_space_size());
            intArrayPointer[2] = static_cast<jint>(sharedMemoryStatistics.read_only_space_used_size());
            jniEnv->ReleaseIntArrayElements(intArray, intArrayPointer, 0);
            return intArray;
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

