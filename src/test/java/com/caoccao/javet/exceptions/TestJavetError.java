/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetError {
    @Test
    public void generateDocument() throws IllegalAccessException, IOException {
        String startSign = "\n.. Error Codes Begin\n\n\n";
        String endSign = "\n.. Error Codes End\n";
        File file = new File(JavetOSUtils.WORKING_DIRECTORY, "docs/reference/troubleshooting/error_codes.rst");
        assertTrue(file.exists());
        assertTrue(file.canRead());
        assertTrue(file.canWrite());
        byte[] originalBuffer = Files.readAllBytes(file.toPath());
        String fileContent = new String(originalBuffer, StandardCharsets.UTF_8);
        assertNotNull(fileContent);
        final int startPosition = fileContent.indexOf(startSign) + startSign.length();
        final int endPosition = fileContent.lastIndexOf(endSign) + 1;
        assertTrue(endPosition >= startPosition && startPosition > 0);
        String[] headerRow = new String[]{"Code", "Type", "Name", "Format"};
        int[] maxLengths = Arrays.stream(headerRow).mapToInt(String::length).toArray();
        Map<Integer, String[]> table = new TreeMap<>();
        Class<?> javetErrorClass = JavetError.class;
        for (Field field : javetErrorClass.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)
                    && field.getType() == javetErrorClass) {
                JavetError javetError = (JavetError) field.get(null);
                String[] row = new String[headerRow.length];
                int code = javetError.getCode();
                table.put(code, row);
                String cell = Integer.toString(code);
                if (maxLengths[0] < cell.length()) {
                    maxLengths[0] = cell.length();
                }
                row[0] = cell;
                cell = javetError.getType().name();
                if (maxLengths[1] < cell.length()) {
                    maxLengths[1] = cell.length();
                }
                row[1] = cell;
                cell = field.getName();
                if (maxLengths[2] < cell.length()) {
                    maxLengths[2] = cell.length();
                }
                row[2] = cell;
                cell = javetError.getFormat();
                if (maxLengths[3] < cell.length()) {
                    maxLengths[3] = cell.length();
                }
                row[3] = cell;
            }
        }
        assertFalse(table.isEmpty());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileContent, 0, startPosition);
        String separator = Arrays.stream(maxLengths)
                .mapToObj(length -> String.join(StringUtils.EMPTY, Collections.nCopies(length, "=")))
                .collect(Collectors.joining(" "));
        stringBuilder.append(separator).append("\n");
        stringBuilder.append(IntStream.range(0, maxLengths.length)
                .mapToObj(i -> String.format("%1$-" + maxLengths[i] + "s", headerRow[i]))
                .collect(Collectors.joining(" "))).append("\n");
        stringBuilder.append(separator).append("\n");
        for (String[] row : table.values()) {
            stringBuilder.append(IntStream.range(0, maxLengths.length)
                    .mapToObj(i -> String.format("%1$-" + maxLengths[i] + "s", row[i]))
                    .collect(Collectors.joining(" "))).append("\n");
        }
        stringBuilder.append(separator).append("\n\n\n");
        stringBuilder.append(fileContent.substring(endPosition));
        byte[] newBuffer = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(file.toPath(), newBuffer, StandardOpenOption.CREATE);
        }
    }
}
