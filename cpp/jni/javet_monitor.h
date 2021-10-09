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
        jintArray GetHeapStatistics(JNIEnv* jniEnv, v8::Isolate* v8Isolate);

#ifdef ENABLE_MONITOR
        namespace CounterType {
            enum CounterType {
                Reserved = 0,
                NewGlobalRef = 1,
                NewWeakCallbackReference = 2,
                NewJavetCallbackContextReference = 3,
                NewPersistentReference = 4,
                NewPersistentCallbackContextReference = 5,
                NewV8Runtime = 6,
                DeleteGlobalRef = 7,
                DeleteWeakCallbackReference = 8,
                DeleteJavetCallbackContextReference = 9,
                DeletePersistentReference = 10,
                DeletePersistentCallbackContextReference = 11,
                DeleteV8Runtime = 12,
                Max = 13,
            };
        };

        class JavetNativeMonitor {
        public:

            JavetNativeMonitor();

            inline void Clear() {
                for (int i = 0; i < CounterType::Max; ++i) {
                    counters[i].store(0);
                }
            }

            jlongArray GetCounters(JNIEnv* jniEnv);

            inline void IncreaseCounter(int counterType) {
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

