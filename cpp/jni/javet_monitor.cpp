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
        struct HeapSpaceStatisticsContainer {
            jobject allocationSpace;
            jobject completableFuture;

            HeapSpaceStatisticsContainer(JNIEnv* jniEnv, jobject completableFuture, jobject allocationSpace) noexcept {
                this->allocationSpace = jniEnv->NewGlobalRef(allocationSpace);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~HeapSpaceStatisticsContainer() {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->DeleteGlobalRef(allocationSpace);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
            }
        };

        struct HeapStatisticsContainer {
            jobject completableFuture;

            HeapStatisticsContainer(JNIEnv* jniEnv, jobject completableFuture) noexcept {
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~HeapStatisticsContainer() {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
            }
        };

        void Initialize(JNIEnv* jniEnv) noexcept {
            jclassV8AllocationSpace = FIND_CLASS(jniEnv, "com/caoccao/javet/enums/V8AllocationSpace");
            jmethodIDV8AllocationSpaceGetIndex = jniEnv->GetMethodID(jclassV8AllocationSpace, "getIndex", "()I");

            jclassCompletableFuture = FIND_CLASS(jniEnv, "java/util/concurrent/CompletableFuture");
            jmethodIDCompletableFutureConstructor = jniEnv->GetMethodID(jclassCompletableFuture, "<init>", "()V");
            jmethodIDCompletableFutureComplete = jniEnv->GetMethodID(jclassCompletableFuture, "complete", "(Ljava/lang/Object;)Z");

            jclassV8HeapSpaceStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics");
            jmethodIDV8HeapSpaceStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapSpaceStatistics, "<init>", "(Ljava/lang/String;JJJJ)V");
            jmethodIDV8HeapSpaceStatisticsSetAllocationSpace = jniEnv->GetMethodID(
                jclassV8HeapSpaceStatistics,
                "setAllocationSpace",
                "(Lcom/caoccao/javet/enums/V8AllocationSpace;)Lcom/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics;");

            jclassV8HeapStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8HeapStatistics");
            jmethodIDV8HeapStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapStatistics, "<init>", "(JJJJJJJJJJJJJJ)V");

            jclassV8SharedMemoryStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8SharedMemoryStatistics");
            jmethodIDV8SharedMemoryStatisticsConstructor = jniEnv->GetMethodID(jclassV8SharedMemoryStatistics, "<init>", "(JJJ)V");
        }

        jobject GetHeapSpaceStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject jAllocationSpace) noexcept {
            jobject jCompletableFuture = jniEnv->NewObject(jclassCompletableFuture, jmethodIDCompletableFutureConstructor);
            auto containerPointer = new HeapSpaceStatisticsContainer(jniEnv, jCompletableFuture, jAllocationSpace);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            if (v8Isolate->IsInUse()) {
                v8Isolate->RequestInterrupt(GetHeapSpaceStatisticsCallback, containerPointer);
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                GetHeapSpaceStatisticsCallback(v8Isolate, containerPointer);
            }
            return jCompletableFuture;
        }

        void GetHeapSpaceStatisticsCallback(v8::Isolate* v8Isolate, void* data) noexcept {
            FETCH_JNI_ENV(GlobalJavaVM);
            v8::HeapSpaceStatistics heapSpaceStatistics;
            auto containerPointer = static_cast<HeapSpaceStatisticsContainer*>(data);
            auto index = jniEnv->CallIntMethod(containerPointer->allocationSpace, jmethodIDV8AllocationSpaceGetIndex);
            v8Isolate->GetHeapSpaceStatistics(&heapSpaceStatistics, static_cast<size_t>(index));
            auto jHeapSpaceStatistics = jniEnv->NewObject(jclassV8HeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsConstructor,
                Javet::Converter::ToJavaString(jniEnv, heapSpaceStatistics.space_name()),
                static_cast<jlong>(heapSpaceStatistics.physical_space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_available_size()),
                static_cast<jlong>(heapSpaceStatistics.space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_used_size()));
            jniEnv->CallObjectMethod(jHeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsSetAllocationSpace, containerPointer->allocationSpace);
            jniEnv->CallBooleanMethod(containerPointer->completableFuture, jmethodIDCompletableFutureComplete, jHeapSpaceStatistics);
            jniEnv->DeleteLocalRef(jHeapSpaceStatistics);
            delete containerPointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
        }

        jobject GetHeapStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate) noexcept {
            jobject jCompletableFuture = jniEnv->NewObject(jclassCompletableFuture, jmethodIDCompletableFutureConstructor);
            auto containerPointer = new HeapStatisticsContainer(jniEnv, jCompletableFuture);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            if (v8Isolate->IsInUse()) {
                v8Isolate->RequestInterrupt(GetHeapStatisticsCallback, containerPointer);
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                GetHeapStatisticsCallback(v8Isolate, containerPointer);
            }
            return jCompletableFuture;
        }

        void GetHeapStatisticsCallback(v8::Isolate* v8Isolate, void* data) noexcept {
            FETCH_JNI_ENV(GlobalJavaVM);
            v8::HeapStatistics heapStatistics;
            auto containerPointer = static_cast<HeapStatisticsContainer*>(data);
            v8Isolate->GetHeapStatistics(&heapStatistics);
            auto jHeapStatistics = jniEnv->NewObject(jclassV8HeapStatistics, jmethodIDV8HeapStatisticsConstructor,
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
            jniEnv->CallBooleanMethod(containerPointer->completableFuture, jmethodIDCompletableFutureComplete, jHeapStatistics);
            jniEnv->DeleteLocalRef(jHeapStatistics);
            delete containerPointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
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

