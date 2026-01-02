/*
 *   Copyright (c) 2021-2026. caoccao.com Sam Cao
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
        static jclass jclassV8AllocationSpace;
        static jmethodID jmethodIDV8AllocationSpaceGetIndex;

        static jclass jclassV8HeapSpaceStatistics;
        static jmethodID jmethodIDV8HeapSpaceStatisticsConstructor;
        static jmethodID jmethodIDV8HeapSpaceStatisticsSetAllocationSpace;

        static jclass jclassV8HeapStatistics;
        static jmethodID jmethodIDV8HeapStatisticsConstructor;

        static jclass jclassV8Host;
        static jmethodID jmethodIDV8HostRegisterV8StatisticsFuture;
        static jmethodID jmethodIDV8HostRequestV8StatisticsFuture;

        static jclass jclassV8SharedMemoryStatistics;
        static jmethodID jmethodIDV8SharedMemoryStatisticsConstructor;

        static jclass jclassV8StatisticsFuture;
        static jmethodID jmethodIDV8StatisticsFutureConstructor;
        static jmethodID jmethodIDV8StatisticsFutureComplete;
        static jmethodID jmethodIDV8StatisticsFutureSetHandle;

        struct HeapSpaceStatisticsContext {
            jobject allocationSpace;
            jobject completableFuture;

            HeapSpaceStatisticsContext(JNIEnv* jniEnv, jobject completableFuture, jobject allocationSpace) noexcept {
                this->allocationSpace = jniEnv->NewGlobalRef(allocationSpace);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~HeapSpaceStatisticsContext() {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->CallVoidMethod(completableFuture, jmethodIDV8StatisticsFutureSetHandle, 0);
                jniEnv->DeleteGlobalRef(allocationSpace);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
            }
        };

        struct HeapStatisticsContext {
            jobject completableFuture;

            HeapStatisticsContext(JNIEnv* jniEnv, jobject completableFuture) noexcept {
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~HeapStatisticsContext() {
                FETCH_JNI_ENV(GlobalJavaVM);
                jniEnv->CallVoidMethod(completableFuture, jmethodIDV8StatisticsFutureSetHandle, 0);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
            }
        };

        void Initialize(JNIEnv* jniEnv) noexcept {
            jclassV8AllocationSpace = FIND_CLASS(jniEnv, "com/caoccao/javet/enums/V8AllocationSpace");
            jmethodIDV8AllocationSpaceGetIndex = jniEnv->GetMethodID(jclassV8AllocationSpace, "getIndex", "()I");

            jclassV8HeapSpaceStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics");
            jmethodIDV8HeapSpaceStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapSpaceStatistics, "<init>", "(Ljava/lang/String;JJJJ)V");
            jmethodIDV8HeapSpaceStatisticsSetAllocationSpace = jniEnv->GetMethodID(
                jclassV8HeapSpaceStatistics,
                "setAllocationSpace",
                "(Lcom/caoccao/javet/enums/V8AllocationSpace;)Lcom/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics;");

            jclassV8HeapStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8HeapStatistics");
            jmethodIDV8HeapStatisticsConstructor = jniEnv->GetMethodID(jclassV8HeapStatistics, "<init>", "(JJJJJJJJJJJJJJ)V");

            jclassV8Host = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/V8Host");
            jmethodIDV8HostRegisterV8StatisticsFuture = jniEnv->GetStaticMethodID(jclassV8Host, "registerV8StatisticsFuture", "(Lcom/caoccao/javet/interop/monitoring/V8StatisticsFuture;)V");
            jmethodIDV8HostRequestV8StatisticsFuture = jniEnv->GetStaticMethodID(jclassV8Host, "requestV8StatisticsFuture", "(J)Z");

            jclassV8SharedMemoryStatistics = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8SharedMemoryStatistics");
            jmethodIDV8SharedMemoryStatisticsConstructor = jniEnv->GetMethodID(jclassV8SharedMemoryStatistics, "<init>", "(JJJ)V");

            jclassV8StatisticsFuture = FIND_CLASS(jniEnv, "com/caoccao/javet/interop/monitoring/V8StatisticsFuture");
            jmethodIDV8StatisticsFutureConstructor = jniEnv->GetMethodID(jclassV8StatisticsFuture, "<init>", "(I)V");
            jmethodIDV8StatisticsFutureComplete = jniEnv->GetMethodID(jclassV8StatisticsFuture, "complete", "(Ljava/lang/Object;)Z");
            jmethodIDV8StatisticsFutureSetHandle = jniEnv->GetMethodID(jclassV8StatisticsFuture, "setHandle", "(J)V");
        }

        jobject GetHeapSpaceStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject jAllocationSpace) noexcept {
            jobject jFuture = jniEnv->NewObject(
                jclassV8StatisticsFuture,
                jmethodIDV8StatisticsFutureConstructor,
                (jint)Javet::Enums::RawPointerType::HeapSpaceStatisticsContext);
            auto contextPointer = new HeapSpaceStatisticsContext(jniEnv, jFuture, jAllocationSpace);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            jniEnv->CallVoidMethod(jFuture, jmethodIDV8StatisticsFutureSetHandle, TO_JAVA_LONG(contextPointer));
            if (v8Isolate->IsInUse()) {
                jniEnv->CallStaticVoidMethod(jclassV8Host, jmethodIDV8HostRegisterV8StatisticsFuture, jFuture);
                v8Isolate->RequestInterrupt(GetHeapSpaceStatisticsAsync, contextPointer);
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                GetHeapSpaceStatisticsSync(jniEnv, v8Isolate, contextPointer);
            }
            return jFuture;
        }

        void GetHeapSpaceStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept {
            FETCH_JNI_ENV(GlobalJavaVM);
            if (jniEnv->CallStaticBooleanMethod(jclassV8Host, jmethodIDV8HostRequestV8StatisticsFuture, TO_JAVA_LONG(data))) {
                GetHeapSpaceStatisticsSync(jniEnv, v8Isolate, data);
            }
            else {
                LOG_DEBUG("Ignore GetHeapSpaceStatisticsAsync().");
            }
        }

        void GetHeapSpaceStatisticsInternal(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject& completableFuture,
            const jobject& allocationSpace) noexcept {
            v8::HeapSpaceStatistics heapSpaceStatistics;
            auto index = jniEnv->CallIntMethod(allocationSpace, jmethodIDV8AllocationSpaceGetIndex);
            v8Isolate->GetHeapSpaceStatistics(&heapSpaceStatistics, static_cast<size_t>(index));
            auto jHeapSpaceStatistics = jniEnv->NewObject(jclassV8HeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsConstructor,
                Javet::Converter::ToJavaString(jniEnv, heapSpaceStatistics.space_name()),
                static_cast<jlong>(heapSpaceStatistics.physical_space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_available_size()),
                static_cast<jlong>(heapSpaceStatistics.space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_used_size()));
            jniEnv->CallObjectMethod(jHeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsSetAllocationSpace, allocationSpace);
            jniEnv->CallBooleanMethod(completableFuture, jmethodIDV8StatisticsFutureComplete, jHeapSpaceStatistics);
            jniEnv->DeleteLocalRef(jHeapSpaceStatistics);
        }

        void GetHeapSpaceStatisticsSync(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            void* data) noexcept {
            auto contextPointer = static_cast<HeapSpaceStatisticsContext*>(data);
            GetHeapSpaceStatisticsInternal(
                jniEnv,
                v8Isolate,
                contextPointer->completableFuture,
                contextPointer->allocationSpace);
            delete contextPointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
        }

        jobject GetHeapStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate) noexcept {
            jobject jFuture = jniEnv->NewObject(
                jclassV8StatisticsFuture,
                jmethodIDV8StatisticsFutureConstructor,
                (jint)Javet::Enums::RawPointerType::HeapStatisticsContext);
            auto contextPointer = new HeapStatisticsContext(jniEnv, jFuture);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            jniEnv->CallVoidMethod(jFuture, jmethodIDV8StatisticsFutureSetHandle, TO_JAVA_LONG(contextPointer));
            if (v8Isolate->IsInUse()) {
                jniEnv->CallStaticVoidMethod(jclassV8Host, jmethodIDV8HostRegisterV8StatisticsFuture, jFuture);
                v8Isolate->RequestInterrupt(GetHeapStatisticsAsync, contextPointer);
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                GetHeapStatisticsSync(jniEnv, v8Isolate, contextPointer);
            }
            return jFuture;
        }

        void GetHeapStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept {
            FETCH_JNI_ENV(GlobalJavaVM);
            if (jniEnv->CallStaticBooleanMethod(jclassV8Host, jmethodIDV8HostRequestV8StatisticsFuture, TO_JAVA_LONG(data))) {
                GetHeapStatisticsSync(jniEnv, v8Isolate, data);
            }
            else {
                LOG_DEBUG("Ignore GetHeapStatisticsAsync().");
            }
        }

        void GetHeapStatisticsInternal(JNIEnv* jniEnv, v8::Isolate* v8Isolate, const jobject& completableFuture) noexcept {
            v8::HeapStatistics heapStatistics;
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
            jniEnv->CallBooleanMethod(completableFuture, jmethodIDV8StatisticsFutureComplete, jHeapStatistics);
            jniEnv->DeleteLocalRef(jHeapStatistics);
        }

        void GetHeapStatisticsSync(JNIEnv* jniEnv, v8::Isolate* v8Isolate, void* data) noexcept {
            auto contextPointer = static_cast<HeapStatisticsContext*>(data);
            GetHeapStatisticsInternal(jniEnv, v8Isolate, contextPointer->completableFuture);
            delete contextPointer;
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

        void RemoveHeapSpaceStatisticsContext(jlong handle) noexcept {
            auto contextPointer = reinterpret_cast<HeapSpaceStatisticsContext*>(handle);
            delete contextPointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
        }

        void RemoveHeapStatisticsContext(jlong handle) noexcept {
            auto contextPointer = reinterpret_cast<HeapStatisticsContext*>(handle);
            delete contextPointer;
            INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
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

