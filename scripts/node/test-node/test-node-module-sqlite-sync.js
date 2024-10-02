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

const sqlite = require("node:sqlite");

const db = new sqlite.DatabaseSync(":memory:");
db.exec(`
  CREATE TABLE data(
    key INTEGER PRIMARY KEY,
    value TEXT
  ) STRICT
`);
const insert = db.prepare("INSERT INTO data (key, value) VALUES (?, ?)");
insert.run(1, "a");
insert.run(2, "b");
const query = db.prepare("SELECT * FROM data ORDER BY key");
const result = query.all();
db.close();
result;
