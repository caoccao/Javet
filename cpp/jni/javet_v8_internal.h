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

/*
 * This header is for developing V8 internal features only.
 * It shall only be included in .cpp files and only be the last header,
 * otherwise compilation errors will take place.
 */

#pragma warning(disable: 4244)
#pragma warning(disable: 4267)
#pragma warning(disable: 4291)
#pragma warning(disable: 4819)
#pragma warning(disable: 4996)
#include <src/objects/objects.h>
#include <src/api/api.h>
#include <src/api/api-inl.h>
#ifdef ENABLE_NODE
#include <src/objects/js-objects-inl.h>
#else
#include <src/objects/js-function-inl.h>
#endif
#include <src/objects/shared-function-info-inl.h>
#include <src/handles/handles-inl.h>
#include <src/strings/string-builder-inl.h>
#pragma warning(default: 4244)
#pragma warning(default: 4267)
#pragma warning(default: 4291)
#pragma warning(default: 4819)
#pragma warning(default: 4996)

