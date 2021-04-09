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

#ifdef ENABLE_MONITOR
Javet::Monitor::JavetNativeMonitor GlobalJavetNativeMonitor;

namespace Javet {
	namespace Monitor {

		JavetNativeMonitor::JavetNativeMonitor() {
			Clear();
		}

		jlongArray JavetNativeMonitor::GetDataArray(JNIEnv* jniEnv) {
			jlongArray returnDataArray = jniEnv->NewLongArray(TypeID::MaxID);
			jniEnv->SetLongArrayRegion(returnDataArray, 0, TypeID::MaxID, dataArray);
			return returnDataArray;
		}
	}
}
#endif

