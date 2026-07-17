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

        enum class StatisticsRequestState {
            Pending,
            Cancelled,
            Claimed,
        };

        struct StatisticsRequestBase {
            std::atomic<StatisticsRequestState> state;
            jlong requestId;
            jint rawPointerTypeId;

            explicit StatisticsRequestBase(jint rawPointerTypeId) noexcept
                : state(StatisticsRequestState::Pending), requestId(0), rawPointerTypeId(rawPointerTypeId) {
            }
        };

        class StatisticsRequestRegistry final {
        public:
            void Cancel(jlong requestId, jint rawPointerTypeId) noexcept {
                std::lock_guard<std::mutex> lock(mutex);
                const auto iterator = requests.find(requestId);
                if (iterator != requests.end() &&
                    iterator->second->rawPointerTypeId == rawPointerTypeId) {
                    iterator->second->state.store(
                        StatisticsRequestState::Cancelled,
                        std::memory_order_release);
                }
            }

            bool Claim(StatisticsRequestBase* request) noexcept {
                std::lock_guard<std::mutex> lock(mutex);
                const auto iterator = requests.find(request->requestId);
                if (iterator == requests.end() || iterator->second != request) {
                    return false;
                }
                requests.erase(iterator);
                return request->state.exchange(
                    StatisticsRequestState::Claimed,
                    std::memory_order_acq_rel) == StatisticsRequestState::Pending;
            }

            void Register(StatisticsRequestBase* request, jlong requestId) noexcept {
                std::lock_guard<std::mutex> lock(mutex);
                request->requestId = requestId;
                requests.emplace(requestId, request);
            }

        private:
            std::mutex mutex;
            std::unordered_map<jlong, StatisticsRequestBase*> requests;
        };

        static StatisticsRequestRegistry statisticsRequestRegistry;

        template<typename Result>
        struct StatisticsRequest final : StatisticsRequestBase {
            jobject argument;
            jobject future;
            Result result;

            StatisticsRequest(
                JNIEnv* jniEnv,
                jobject future,
                jint rawPointerTypeId,
                jobject argument = nullptr) noexcept
                : StatisticsRequestBase(rawPointerTypeId), argument(nullptr), future(nullptr), result() {
                if (argument != nullptr) {
                    this->argument = jniEnv->NewGlobalRef(argument);
                    INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
                }
                this->future = jniEnv->NewGlobalRef(future);
                INCREASE_COUNTER(Javet::Monitor::CounterType::NewGlobalRef);
            }

            ~StatisticsRequest() {
                auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
                if (!jniEnvScope) {
                    LOG_ERROR("StatisticsRequest::~StatisticsRequest(): JNI environment is unavailable.");
                    INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
                    return;
                }
                JNIEnv* jniEnv = jniEnvScope.Get();
                jniEnv->CallVoidMethod(future, jmethodIDV8StatisticsFutureSetHandle, 0);
                if (argument != nullptr) {
                    jniEnv->DeleteGlobalRef(argument);
                    INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                }
                jniEnv->DeleteGlobalRef(future);
                INCREASE_COUNTER(Javet::Monitor::CounterType::DeleteGlobalRef);
                INCREASE_COUNTER(Javet::Monitor::CounterType::Delete);
            }
        };

        template<typename Request>
        std::shared_ptr<Request> TakeStatisticsRequest(void* data) noexcept {
            auto requestHolder = std::unique_ptr<std::shared_ptr<Request>>(
                static_cast<std::shared_ptr<Request>*>(data));
            return std::move(*requestHolder);
        }

        bool Initialize(JNIEnv* jniEnv) noexcept {
            JNIInitializer jniInitializer(jniEnv);
            jniInitializer.FindGlobalClass(jclassV8AllocationSpace, "com/caoccao/javet/enums/V8AllocationSpace");
            jniInitializer.GetMethodID(jmethodIDV8AllocationSpaceGetIndex, jclassV8AllocationSpace, "getIndex", "()I");

            jniInitializer.FindGlobalClass(jclassV8HeapSpaceStatistics, "com/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics");
            jniInitializer.GetMethodID(jmethodIDV8HeapSpaceStatisticsConstructor, jclassV8HeapSpaceStatistics, "<init>", "(Ljava/lang/String;JJJJ)V");
            jniInitializer.GetMethodID(
                jmethodIDV8HeapSpaceStatisticsSetAllocationSpace,
                jclassV8HeapSpaceStatistics,
                "setAllocationSpace",
                "(Lcom/caoccao/javet/enums/V8AllocationSpace;)Lcom/caoccao/javet/interop/monitoring/V8HeapSpaceStatistics;");

            jniInitializer.FindGlobalClass(jclassV8HeapStatistics, "com/caoccao/javet/interop/monitoring/V8HeapStatistics");
            jniInitializer.GetMethodID(jmethodIDV8HeapStatisticsConstructor, jclassV8HeapStatistics, "<init>", "(JJJJJJJJJJJJJJ)V");

            jniInitializer.FindGlobalClass(jclassV8Host, "com/caoccao/javet/interop/V8Host");
            jniInitializer.GetStaticMethodID(jmethodIDV8HostGetNextV8StatisticsRequestId, jclassV8Host, "getNextV8StatisticsRequestId", "()J");
            jniInitializer.GetStaticMethodID(jmethodIDV8HostRegisterV8StatisticsFuture, jclassV8Host, "registerV8StatisticsFuture", "(Lcom/caoccao/javet/interop/monitoring/V8StatisticsFuture;)V");
            jniInitializer.GetStaticMethodID(jmethodIDV8HostRequestV8StatisticsFuture, jclassV8Host, "requestV8StatisticsFuture", "(J)Z");

            jniInitializer.FindGlobalClass(jclassV8SharedMemoryStatistics, "com/caoccao/javet/interop/monitoring/V8SharedMemoryStatistics");
            jniInitializer.GetMethodID(jmethodIDV8SharedMemoryStatisticsConstructor, jclassV8SharedMemoryStatistics, "<init>", "(JJJ)V");

            jniInitializer.FindGlobalClass(jclassV8StatisticsFuture, "com/caoccao/javet/interop/monitoring/V8StatisticsFuture");
            jniInitializer.GetMethodID(jmethodIDV8StatisticsFutureConstructor, jclassV8StatisticsFuture, "<init>", "(I)V");
            jniInitializer.GetMethodID(jmethodIDV8StatisticsFutureComplete, jclassV8StatisticsFuture, "complete", "(Ljava/lang/Object;)Z");
            jniInitializer.GetMethodID(jmethodIDV8StatisticsFutureSetHandle, jclassV8StatisticsFuture, "setHandle", "(J)V");
            return jniInitializer.IsValid();
        }

        struct HeapSpaceStatisticsTraits final {
            using Result = v8::HeapSpaceStatistics;
            static constexpr jint RawPointerTypeId =
                Javet::Enums::RawPointerType::HeapSpaceStatisticsContext;

            static jobject BuildJavaResult(
                JNIEnv* jniEnv,
                jobject allocationSpace,
                Result& result) noexcept {
                jstring spaceName = Javet::Converter::ToJavaStringFromUtf8(
                    jniEnv,
                    result.space_name());
                auto javaResult = jniEnv->NewObject(
                    jclassV8HeapSpaceStatistics,
                    jmethodIDV8HeapSpaceStatisticsConstructor,
                    spaceName,
                    static_cast<jlong>(result.physical_space_size()),
                    static_cast<jlong>(result.space_available_size()),
                    static_cast<jlong>(result.space_size()),
                    static_cast<jlong>(result.space_used_size()));
                DELETE_LOCAL_REF(jniEnv, spaceName);
                jobject javaResultWithAllocationSpace = jniEnv->CallObjectMethod(
                    javaResult,
                    jmethodIDV8HeapSpaceStatisticsSetAllocationSpace,
                    allocationSpace);
                DELETE_LOCAL_REF(jniEnv, javaResultWithAllocationSpace);
                return javaResult;
            }

            static void Collect(
                JNIEnv* jniEnv,
                v8::Isolate* v8Isolate,
                jobject allocationSpace,
                Result& result) noexcept {
                const auto index = jniEnv->CallIntMethod(
                    allocationSpace,
                    jmethodIDV8AllocationSpaceGetIndex);
                v8Isolate->GetHeapSpaceStatistics(&result, static_cast<size_t>(index));
            }
        };

        struct HeapStatisticsTraits final {
            using Result = v8::HeapStatistics;
            static constexpr jint RawPointerTypeId =
                Javet::Enums::RawPointerType::HeapStatisticsContext;

            static jobject BuildJavaResult(
                JNIEnv* jniEnv,
                jobject,
                Result& result) noexcept {
                return jniEnv->NewObject(
                    jclassV8HeapStatistics,
                    jmethodIDV8HeapStatisticsConstructor,
                    static_cast<jlong>(result.does_zap_garbage()),
                    static_cast<jlong>(result.external_memory()),
                    static_cast<jlong>(result.heap_size_limit()),
                    static_cast<jlong>(result.malloced_memory()),
                    static_cast<jlong>(result.number_of_detached_contexts()),
                    static_cast<jlong>(result.number_of_native_contexts()),
                    static_cast<jlong>(result.peak_malloced_memory()),
                    static_cast<jlong>(result.total_available_size()),
                    static_cast<jlong>(result.total_global_handles_size()),
                    static_cast<jlong>(result.total_heap_size()),
                    static_cast<jlong>(result.total_heap_size_executable()),
                    static_cast<jlong>(result.total_physical_size()),
                    static_cast<jlong>(result.used_global_handles_size()),
                    static_cast<jlong>(result.used_heap_size()));
            }

            static void Collect(
                JNIEnv*,
                v8::Isolate* v8Isolate,
                jobject,
                Result& result) noexcept {
                v8Isolate->GetHeapStatistics(&result);
            }
        };

        struct SharedMemoryStatisticsTraits final {
            using Result = v8::SharedMemoryStatistics;
            static constexpr jint RawPointerTypeId =
                Javet::Enums::RawPointerType::SharedMemoryStatisticsContext;

            static jobject BuildJavaResult(
                JNIEnv* jniEnv,
                jobject,
                Result& result) noexcept {
                return jniEnv->NewObject(
                    jclassV8SharedMemoryStatistics,
                    jmethodIDV8SharedMemoryStatisticsConstructor,
                    static_cast<jlong>(result.read_only_space_physical_size()),
                    static_cast<jlong>(result.read_only_space_size()),
                    static_cast<jlong>(result.read_only_space_used_size()));
            }

            static void Collect(
                JNIEnv*,
                v8::Isolate* v8Isolate,
                jobject,
                Result& result) noexcept {
                // In multi-cage pointer compression mode, ReadOnlyHeap statistics
                // are owned by the calling thread's IsolateGroup (a thread_local).
                // The isolate scope is needed for the sync path; an interrupt is
                // already running inside the isolate.
                v8::Isolate::Scope v8IsolateScope(v8Isolate);
                v8::V8::GetSharedMemoryStatistics(&result);
            }
        };

        template<typename Traits>
        using TypedStatisticsRequest = StatisticsRequest<typename Traits::Result>;

        template<typename Traits>
        void CompleteStatisticsRequest(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            TypedStatisticsRequest<Traits>* request) noexcept {
            Traits::Collect(jniEnv, v8Isolate, request->argument, request->result);
            jobject javaResult = Traits::BuildJavaResult(
                jniEnv,
                request->argument,
                request->result);
            jniEnv->CallBooleanMethod(
                request->future,
                jmethodIDV8StatisticsFutureComplete,
                javaResult);
            DELETE_LOCAL_REF(jniEnv, javaResult);
        }

        template<typename Traits>
        void ProcessStatisticsRequestAsync(v8::Isolate* v8Isolate, void* data) noexcept {
            auto request = TakeStatisticsRequest<TypedStatisticsRequest<Traits>>(data);
            auto jniEnvScope = JNIEnvScope::Acquire(GlobalJavaVM);
            if (!jniEnvScope) {
                LOG_ERROR("ProcessStatisticsRequestAsync(): JNI environment is unavailable.");
                statisticsRequestRegistry.Claim(request.get());
                return;
            }
            JNIEnv* jniEnv = jniEnvScope.Get();
            const bool requested = jniEnv->CallStaticBooleanMethod(
                jclassV8Host,
                jmethodIDV8HostRequestV8StatisticsFuture,
                request->requestId);
            const bool claimed = statisticsRequestRegistry.Claim(request.get());
            if (requested && claimed) {
                CompleteStatisticsRequest<Traits>(jniEnv, v8Isolate, request.get());
            }
            else {
                LOG_DEBUG("Ignore ProcessStatisticsRequestAsync().");
            }
        }

        template<typename Traits>
        jobject CreateStatisticsRequest(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            jobject argument = nullptr) noexcept {
            jobject javaFuture = jniEnv->NewObject(
                jclassV8StatisticsFuture,
                jmethodIDV8StatisticsFutureConstructor,
                Traits::RawPointerTypeId);
            auto request = std::make_shared<TypedStatisticsRequest<Traits>>(
                jniEnv,
                javaFuture,
                Traits::RawPointerTypeId,
                argument);
            INCREASE_COUNTER(Javet::Monitor::CounterType::New);
            const jlong requestId = jniEnv->CallStaticLongMethod(
                jclassV8Host,
                jmethodIDV8HostGetNextV8StatisticsRequestId);
            statisticsRequestRegistry.Register(request.get(), requestId);
            jniEnv->CallVoidMethod(
                javaFuture,
                jmethodIDV8StatisticsFutureSetHandle,
                requestId);
            if (v8Isolate->IsInUse()) {
                auto requestHolder =
                    std::make_unique<std::shared_ptr<TypedStatisticsRequest<Traits>>>(request);
                jniEnv->CallStaticVoidMethod(
                    jclassV8Host,
                    jmethodIDV8HostRegisterV8StatisticsFuture,
                    javaFuture);
                v8Isolate->RequestInterrupt(
                    ProcessStatisticsRequestAsync<Traits>,
                    requestHolder.release());
            }
            else {
                auto v8Locker = v8::Locker(v8Isolate);
                if (statisticsRequestRegistry.Claim(request.get())) {
                    CompleteStatisticsRequest<Traits>(jniEnv, v8Isolate, request.get());
                }
            }
            return javaFuture;
        }

        jobject GetHeapSpaceStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject allocationSpace) noexcept {
            return CreateStatisticsRequest<HeapSpaceStatisticsTraits>(
                jniEnv,
                v8Isolate,
                allocationSpace);
        }

        jobject GetHeapStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate) noexcept {
            return CreateStatisticsRequest<HeapStatisticsTraits>(jniEnv, v8Isolate);
        }

        jobject GetV8SharedMemoryStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate) noexcept {
            return CreateStatisticsRequest<SharedMemoryStatisticsTraits>(jniEnv, v8Isolate);
        }

        void RemoveStatisticsContext(jlong handle, jint rawPointerTypeId) noexcept {
            statisticsRequestRegistry.Cancel(handle, rawPointerTypeId);
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
