/*
 *   Copyright (c) 2021-2025. caoccao.com Sam Cao
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

#pragma once

#include <atomic>
#include <jni.h>
#include "javet_v8.h"

#ifdef ENABLE_MONITOR
#define INCREASE_COUNTER(counterType) GlobalJavetNativeMonitor.IncreaseCounter(counterType)
#else
#define INCREASE_COUNTER(counterType)
#endif

namespace Javet {
    namespace Monitor {
        void Initialize(JNIEnv* jniEnv) noexcept;

        jobject GetHeapSpaceStatistics(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject allocationSpaceIndex) noexcept;
        void GetHeapSpaceStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept;
        void GetHeapSpaceStatisticsInternal(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject& completableFuture,
            const jobject& allocationSpace) noexcept;
        void GetHeapSpaceStatisticsSync(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            void* data) noexcept;

        jobject GetHeapStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate) noexcept;
        void GetHeapStatisticsAsync(v8::Isolate* v8Isolate, void* data) noexcept;
        void GetHeapStatisticsInternal(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            const jobject& completableFuture) noexcept;
        void GetHeapStatisticsSync(
            JNIEnv* jniEnv,
            v8::Isolate* v8Isolate,
            void* data) noexcept;

        jobject GetV8SharedMemoryStatistics(JNIEnv* jniEnv) noexcept;

        void RemoveHeapSpaceStatisticsContext(jlong handle) noexcept;
        void RemoveHeapStatisticsContext(jlong handle) noexcept;

#ifdef ENABLE_MONITOR
        namespace CounterType {
            enum CounterType {
                Reserved = 0,
                New = 1,
                NewGlobalRef = 2,
                NewWeakCallbackReference = 3,
                NewJavetCallbackContextReference = 4,
                NewPersistentReference = 5,
                NewPersistentCallbackContextReference = 6,
                NewV8Runtime = 7,
                Delete = 8,
                DeleteGlobalRef = 9,
                DeleteWeakCallbackReference = 10,
                DeleteJavetCallbackContextReference = 11,
                DeletePersistentReference = 12,
                DeletePersistentCallbackContextReference = 13,
                DeleteV8Runtime = 14,
                Max = 15,
            };
        };

        class JavetNativeMonitor {
        public:

            JavetNativeMonitor() noexcept;

            inline void Clear() noexcept {
                for (int i = 0; i < CounterType::Max; ++i) {
                    counters[i].store(0);
                }
            }

            jlongArray GetCounters(JNIEnv* jniEnv) noexcept;

            inline void IncreaseCounter(int counterType) noexcept {
                counters[counterType]++;
            }
        private:
            std::atomic<jlong> counters[CounterType::Max];
        };
#endif

    }
}

#ifdef ENABLE_MONITOR
extern Javet::Monitor::JavetNativeMonitor GlobalJavetNativeMonitor;
#endif

