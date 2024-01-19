/*
 *   Copyright (c) 2021-2024. caoccao.com Sam Cao
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

"use strict";

const vm = require('vm');

const context = {
    a: '1',
    b: 2,
};

vm.createContext(context);
const script = new vm.Script('a = "x", b += 1;');
script.runInContext(context);
const results = [context, typeof a, typeof b];
results; // [{a:'x',b:3},'undefined','undefined']
