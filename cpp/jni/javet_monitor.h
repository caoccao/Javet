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

#include <jni.h>

#ifdef ENABLE_MONITOR
#define INCREASE_COUNTER(counterType) GlobalJavetNativeMonitor.IncreaseCounter(counterType)
#else
#define INCREASE_COUNTER(counterType)
#endif

#ifdef ENABLE_MONITOR
namespace Javet {
	namespace Monitor {
		enum CounterType {
			Reserved = 0,
			NewWeakCallbackReference = 1,
			NewJavetCallbackContextReference = 2,
			NewPersistentReference = 3,
			NewPersistentCallbackContextReference = 4,
			NewV8Runtime = 5,
			DeleteWeakCallbackReference = 6,
			DeleteJavetCallbackContextReference = 7,
			DeletePersistentReference = 8,
			DeletePersistentCallbackContextReference = 9,
			DeleteV8Runtime = 10,
			Max = 11,
		};

		class JavetNativeMonitor {
		public:

			JavetNativeMonitor();

			inline void Clear() {
				for (int i = 0; i < CounterType::Max; ++i) {
					counters[i] = 0;
				}
			}

			jlongArray GetCounters(JNIEnv* jniEnv);

			inline void IncreaseCounter(int counterType) {
				counters[counterType]++;
			}
		private:
			jlong counters[CounterType::Max];
		};

	}
}

extern Javet::Monitor::JavetNativeMonitor GlobalJavetNativeMonitor;
#endif
