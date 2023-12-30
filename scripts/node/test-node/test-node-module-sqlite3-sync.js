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

const sqlite3 = require('sqlite3');

const db = new sqlite3.Database(':memory:', (e) => {
  if (e) {
    return console.error(e.message);
  }
  console.log('Connected to the in-memory sqlite3 database.');
});

db.close((e) => {
  if (e) {
    return console.error(e.message);
  }
  console.log('Closed the database connection.');
});
