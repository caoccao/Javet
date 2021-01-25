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

#include "javet_exceptions.h"

namespace Javet {
	namespace Exceptions {
		void initializeJavetExceptions(JNIEnv* jniEnv) {
			/*
			 @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
			 @see https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
			*/

			jclassJavetV8RuntimeLockConflictException = (jclass)jniEnv->NewGlobalRef(jniEnv->FindClass("com/caoccao/javet/exceptions/JavetV8RuntimeLockConflictException"));
		}

		void throwJavetV8RuntimeLockConflictException(JNIEnv* jniEnv, const char* message) {
			jniEnv->ThrowNew(jclassJavetV8RuntimeLockConflictException, message);
		}
	}
}
