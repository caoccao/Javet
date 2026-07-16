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

#include <memory>
#include <mutex>
#include <unordered_map>

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
        static jmethodID jmethodIDV8HostGetNextV8StatisticsRequestId;
        static jmethodID jmethodIDV8HostRegisterV8StatisticsFuture;
        static jmethodID jmethodIDV8HostRequestV8StatisticsFuture;

        static jclass jclassV8SharedMemoryStatistics;
        static jmethodID jmethodIDV8SharedMemoryStatisticsConstructor;

        static jclass jclassV8StatisticsFuture;
        static jmethodID jmethodIDV8StatisticsFutureConstructor;
        static jmethodID jmethodIDV8StatisticsFutureComplete;
        static jmethodID jmethodIDV8StatisticsFutureSetHandle;

        struct StatisticsRequest {
            std::atomic_bool cancelled;
            jlong requestId;
            jint rawPointerTypeId;

            explicit StatisticsRequest(jint rawPointerTypeId) noexcept
                : cancelled(false), requestId(0), rawPointerTypeId(rawPointerTypeId) {
            }
        };

        static std::mutex statisticsRequestMutex;
        static std::unordered_map<jlong, StatisticsRequest*> statisticsRequestMap;

        void CancelStatisticsRequest(jlong requestId, jint rawPointerTypeId) noexcept {
            std::lock_guard<std::mutex> lock(statisticsRequestMutex);
            const auto iterator = statisticsRequestMap.find(requestId);
            if (iterator != statisticsRequestMap.end() &&
                iterator->second->rawPointerTypeId == rawPointerTypeId) {
                iterator->second->cancelled.store(true, std::memory_order_release);
            }
        }

        bool ClaimStatisticsRequest(StatisticsRequest* request) noexcept {
            std::lock_guard<std::mutex> lock(statisticsRequestMutex);
            const auto iterator = statisticsRequestMap.find(request->requestId);
            if (iterator == statisticsRequestMap.end() || iterator->second != request) {
                return false;
            }
            statisticsRequestMap.erase(iterator);
            return !request->cancelled.load(std::memory_order_acquire);
        }

        void RegisterStatisticsRequest(StatisticsRequest* request, jlong requestId) noexcept {
            std::lock_guard<std::mutex> lock(statisticsRequestMutex);
            request->requestId = requestId;
            statisticsRequestMap.emplace(requestId, request);
        }

        template<typename T>
        std::shared_ptr<T> TakeStatisticsRequest(void* data) noexcept {
            auto requestHolder = std::unique_ptr<std::shared_ptr<T>>(
                static_cast<std::shared_ptr<T>*>(data));
            return std::move(*requestHolder);
        }

        struct HeapSpaceStatisticsContext : StatisticsRequest {
            jobject allocationSpace;
            jobject completableFuture;

            HeapSpaceStatisticsContext(JNIEnv* jniEnv, jobject completableFuture, jobject allocationSpace) noexcept
                : StatisticsRequest(Javet::Enums::RawPointerType::HeapSpaceStatisticsContext) {
                this->allocationSpace = jniEnv->NewGlobalRef(allocationSpace);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~HeapSpaceStatisticsContext() {
                auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
                if (!jniEnvScope) {
                    LOG_ERROR("HeapSpaceStatisticsContext::~HeapSpaceStatisticsContext(): JNI environment is unavailable.");
                    INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
                    return;
                }
                JNIEnv* jniEnv = jniEnvScope.Get();
                jniEnv->CallVoidMethod(completableFuture, jmethodIDV8StatisticsFutureSetHandle, 0);
                jniEnv->DeleteGlobalRef(allocationSpace);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
            }
        };

        struct HeapStatisticsContext : StatisticsRequest {
            jobject completableFuture;

            HeapStatisticsContext(JNIEnv* jniEnv, jobject completableFuture) noexcept
                : StatisticsRequest(Javet::Enums::RawPointerType::HeapStatisticsContext) {
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~HeapStatisticsContext() {
                auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
                if (!jniEnvScope) {
                    LOG_ERROR("HeapStatisticsContext::~HeapStatisticsContext(): JNI environment is unavailable.");
                    INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
                    return;
                }
                JNIEnv* jniEnv = jniEnvScope.Get();
                jniEnv->CallVoidMethod(completableFuture, jmethodIDV8StatisticsFutureSetHandle, 0);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
            }
        };

        struct SharedMemoryStatisticsContext : StatisticsRequest {
            jobject completableFuture;

            SharedMemoryStatisticsContext(JNIEnv* jniEnv, jobject completableFuture) noexcept
                : StatisticsRequest(Javet::Enums::RawPointerType::SharedMemoryStatisticsContext) {
                this->completableFuture = jniEnv->NewGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~SharedMemoryStatisticsContext() {
                auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
                if (!jniEnvScope) {
                    LOG_ERROR("SharedMemoryStatisticsContext::~SharedMemoryStatisticsContext(): JNI environment is unavailable.");
                    INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
                    return;
                }
                JNIEnv* jniEnv = jniEnvScope.Get();
                jniEnv->CallVoidMethod(completableFuture, jmethodIDV8StatisticsFutureSetHandle, 0);
                jniEnv->DeleteGlobalRef(completableFuture);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
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
            jmethodIDV8HostGetNextV8StatisticsRequestId = jniEnv->GetStaticMethodID(jclassV8Host, "getNextV8StatisticsRequestId", "()J");
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
            auto context = std::make_shared<HeapSpaceStatisticsContext>(jniEnv, jFuture, jAllocationSpace);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            const jlong requestId = jniEnv->CallStaticLongMethod(
                jclassV8Host,
                jmethodIDV8HostGetNextV8StatisticsRequestId);
            RegisterStatisticsRequest(context.get(), requestId);
            jniEnv->CallVoidMethod(jFuture, jmethodIDV8StatisticsFutureSetHandle, requestId);
            if (v8Isolate->IsInUse()) {
                auto requestHolder = std::make_unique<std::shared_ptr<HeapSpaceStatisticsContext>>(context);
                jniEnv->CallStaticVoidMethod(jclassV8Host, jmethodIDV8HostRegisterV8StatisticsFuture, jFuture);
                v8Isolate->RequestInterrupt(GetHeapSpaceStatisticsAsync, requestHolder.release());
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                if (ClaimStatisticsRequest(context.get())) {
                    GetHeapSpaceStatisticsSync(jniEnv, v8Isolate, context.get());
                }
            }
            return jFuture;
        }

        void GetHeapSpaceStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept {
            auto context = TakeStatisticsRequest<HeapSpaceStatisticsContext>(data);
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("GetHeapSpaceStatisticsAsync(): JNI environment is unavailable.");
                ClaimStatisticsRequest(context.get());
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            const bool requested = jniEnv->CallStaticBooleanMethod(
                jclassV8Host,
                jmethodIDV8HostRequestV8StatisticsFuture,
                context->requestId);
            const bool claimed = ClaimStatisticsRequest(context.get());
            if (requested && claimed) {
                GetHeapSpaceStatisticsSync(jniEnv, v8Isolate, context.get());
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
            jstring spaceName = Javet::Converter::ToJavaString(
                jniEnv, heapSpaceStatistics.space_name());
            auto jHeapSpaceStatistics = jniEnv->NewObject(jclassV8HeapSpaceStatistics, jmethodIDV8HeapSpaceStatisticsConstructor,
                spaceName,
                static_cast<jlong>(heapSpaceStatistics.physical_space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_available_size()),
                static_cast<jlong>(heapSpaceStatistics.space_size()),
                static_cast<jlong>(heapSpaceStatistics.space_used_size()));
            DELETE_LOCAL_REF(jniEnv, spaceName);
            jobject heapSpaceStatisticsWithAllocationSpace = jniEnv->CallObjectMethod(
                jHeapSpaceStatistics,
                jmethodIDV8HeapSpaceStatisticsSetAllocationSpace,
                allocationSpace);
            DELETE_LOCAL_REF(jniEnv, heapSpaceStatisticsWithAllocationSpace);
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
        }

        jobject GetHeapStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate) noexcept {
            jobject jFuture = jniEnv->NewObject(
                jclassV8StatisticsFuture,
                jmethodIDV8StatisticsFutureConstructor,
                (jint)Javet::Enums::RawPointerType::HeapStatisticsContext);
            auto context = std::make_shared<HeapStatisticsContext>(jniEnv, jFuture);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            const jlong requestId = jniEnv->CallStaticLongMethod(
                jclassV8Host,
                jmethodIDV8HostGetNextV8StatisticsRequestId);
            RegisterStatisticsRequest(context.get(), requestId);
            jniEnv->CallVoidMethod(jFuture, jmethodIDV8StatisticsFutureSetHandle, requestId);
            if (v8Isolate->IsInUse()) {
                auto requestHolder = std::make_unique<std::shared_ptr<HeapStatisticsContext>>(context);
                jniEnv->CallStaticVoidMethod(jclassV8Host, jmethodIDV8HostRegisterV8StatisticsFuture, jFuture);
                v8Isolate->RequestInterrupt(GetHeapStatisticsAsync, requestHolder.release());
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                if (ClaimStatisticsRequest(context.get())) {
                    GetHeapStatisticsSync(jniEnv, v8Isolate, context.get());
                }
            }
            return jFuture;
        }

        void GetHeapStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept {
            auto context = TakeStatisticsRequest<HeapStatisticsContext>(data);
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("GetHeapStatisticsAsync(): JNI environment is unavailable.");
                ClaimStatisticsRequest(context.get());
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            const bool requested = jniEnv->CallStaticBooleanMethod(
                jclassV8Host,
                jmethodIDV8HostRequestV8StatisticsFuture,
                context->requestId);
            const bool claimed = ClaimStatisticsRequest(context.get());
            if (requested && claimed) {
                GetHeapStatisticsSync(jniEnv, v8Isolate, context.get());
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
        }

        jobject GetV8SharedMemoryStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate) noexcept {
            jobject jFuture = jniEnv->NewObject(
                jclassV8StatisticsFuture,
                jmethodIDV8StatisticsFutureConstructor,
                (jint)Javet::Enums::RawPointerType::SharedMemoryStatisticsContext);
            auto context = std::make_shared<SharedMemoryStatisticsContext>(jniEnv, jFuture);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            const jlong requestId = jniEnv->CallStaticLongMethod(
                jclassV8Host,
                jmethodIDV8HostGetNextV8StatisticsRequestId);
            RegisterStatisticsRequest(context.get(), requestId);
            jniEnv->CallVoidMethod(jFuture, jmethodIDV8StatisticsFutureSetHandle, requestId);
            if (v8Isolate->IsInUse()) {
                auto requestHolder = std::make_unique<std::shared_ptr<SharedMemoryStatisticsContext>>(context);
                jniEnv->CallStaticVoidMethod(jclassV8Host, jmethodIDV8HostRegisterV8StatisticsFuture, jFuture);
                v8Isolate->RequestInterrupt(GetV8SharedMemoryStatisticsAsync, requestHolder.release());
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                if (ClaimStatisticsRequest(context.get())) {
                    GetV8SharedMemoryStatisticsSync(jniEnv, v8Isolate, context.get());
                }
            }
            return jFuture;
        }

        void GetV8SharedMemoryStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept {
            auto context = TakeStatisticsRequest<SharedMemoryStatisticsContext>(data);
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("GetV8SharedMemoryStatisticsAsync(): JNI environment is unavailable.");
                ClaimStatisticsRequest(context.get());
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            const bool requested = jniEnv->CallStaticBooleanMethod(
                jclassV8Host,
                jmethodIDV8HostRequestV8StatisticsFuture,
                context->requestId);
            const bool claimed = ClaimStatisticsRequest(context.get());
            if (requested && claimed) {
                GetV8SharedMemoryStatisticsSync(jniEnv, v8Isolate, context.get());
            }
            else {
                LOG_DEBUG("Ignore GetV8SharedMemoryStatisticsAsync().");
            }
        }

        void GetV8SharedMemoryStatisticsInternal(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject& completableFuture) noexcept {
            v8::SharedMemoryStatistics sharedMemoryStatistics;
            // In multi-cage pointer compression mode, ReadOnlyHeap statistics
            // are owned by the calling thread's IsolateGroup (a thread_local).
            // The interrupt callback / locker-held sync path runs with this
            // isolate's group as `current`, so the V8 API resolves correctly.
            // The isolate scope is needed for the sync (non-RequestInterrupt)
            // path; the interrupt is already inside the isolate.
            v8::Isolate::Scope v8IsolateScope(v8Isolate);
            v8::V8::GetSharedMemoryStatistics(&sharedMemoryStatistics);
            auto jSharedMemoryStatistics = jniEnv->NewObject(jclassV8SharedMemoryStatistics, jmethodIDV8SharedMemoryStatisticsConstructor,
                static_cast<jlong>(sharedMemoryStatistics.read_only_space_physical_size()),
                static_cast<jlong>(sharedMemoryStatistics.read_only_space_size()),
                static_cast<jlong>(sharedMemoryStatistics.read_only_space_used_size()));
            jniEnv->CallBooleanMethod(completableFuture, jmethodIDV8StatisticsFutureComplete, jSharedMemoryStatistics);
            jniEnv->DeleteLocalRef(jSharedMemoryStatistics);
        }

        void GetV8SharedMemoryStatisticsSync(JNIEnv* jniEnv, v8::Isolate* v8Isolate, void* data) noexcept {
            auto contextPointer = static_cast<SharedMemoryStatisticsContext*>(data);
            GetV8SharedMemoryStatisticsInternal(jniEnv, v8Isolate, contextPointer->completableFuture);
        }

        void RemoveHeapSpaceStatisticsContext(jlong handle) noexcept {
            CancelStatisticsRequest(
                handle,
                Javet::Enums::RawPointerType::HeapSpaceStatisticsContext);
        }

        void RemoveHeapStatisticsContext(jlong handle) noexcept {
            CancelStatisticsRequest(
                handle,
                Javet::Enums::RawPointerType::HeapStatisticsContext);
        }

        void RemoveV8SharedMemoryStatisticsContext(jlong handle) noexcept {
            CancelStatisticsRequest(
                handle,
                Javet::Enums::RawPointerType::SharedMemoryStatisticsContext);
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
