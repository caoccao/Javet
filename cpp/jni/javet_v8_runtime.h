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
#include "javet_converter.h"
#include "javet_exceptions.h"
#include "javet_logging.h"
#include "javet_types.h"
#include "javet_v8.h"

namespace Javet {
	namespace Inspector {
		class JavetInspector;
	}

	class V8Runtime;
	class V8Scope;

	class V8Runtime {
	public:
		v8::Isolate* v8Isolate;
		jobject externalV8Runtime;
		V8PersistentContext v8Context;
		V8PersistentObject v8GlobalObject;
		std::unique_ptr<Javet::Inspector::JavetInspector> v8Inspector;

		void CloseV8Isolate();

		inline void CreateV8Isolate() {
			v8::Isolate::CreateParams createParams;
			createParams.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();
			v8Isolate = v8::Isolate::New(createParams);
		}

		static inline V8Runtime* FromHandle(jlong handle) {
			return reinterpret_cast<V8Runtime*>(handle);
		}

		static inline V8Runtime* FromV8Context(V8LocalContext v8Context) {
			return reinterpret_cast<V8Runtime*>(v8Context->GetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME)->ToBigInt(v8Context).ToLocalChecked()->Int64Value());
		}

		/*
		* Shared V8 locker is for implicit mode.
		* Javet manages the lock automatically.
		*/
		inline std::shared_ptr<v8::Locker> GetSharedV8Locker() {
			return v8Locker ? v8Locker : std::make_shared<v8::Locker>(v8Isolate);
		}

		/*
		* Unique V8 locker is for explicit mode.
		* Application manages the lock.
		*/
		inline std::unique_ptr<v8::Locker> GetUniqueV8Locker() {
			return std::make_unique<v8::Locker>(v8Isolate);
		}

		inline V8LocalContext GetV8LocalContext() {
			return v8Context.Get(v8Isolate);
		}

		inline bool IsLocked() {
			return (bool)v8Locker;
		}

		inline void Lock() {
			v8Locker.reset(new v8::Locker(v8Isolate));
		}

		void Register(V8LocalContext v8Context) {
			v8Context->SetEmbedderData(EMBEDDER_DATA_INDEX_V8_RUNTIME, v8::BigInt::New(v8Isolate, TO_NATIVE_INT_64(this)));
		}

		void ResetV8Context(JNIEnv* jniEnv, jstring mGlobalName);

		inline jobject SafeToExternalV8Value(JNIEnv* jniEnv, V8LocalContext v8Context, V8LocalValue v8Value) {
			try {
				return Javet::Converter::ToExternalV8Value(jniEnv, externalV8Runtime, v8Context, v8Value);
			}
			catch (const std::exception& e) {
				LOG_ERROR(e.what());
				Javet::Exceptions::ThrowJavetConverterException(jniEnv, e.what());
			}
			return nullptr;
		}

		inline void Unlock() {
			v8Locker.reset();
		}

		virtual ~V8Runtime();

	private:
		std::shared_ptr<v8::Locker> v8Locker;
	};
}

