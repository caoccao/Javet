package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.JavetOSUtils;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestJavetError {
    @Test
    public void generateDocument() throws IllegalAccessException, IOException {
        String startSign = "\n.. Error Codes Begin\n\n\n";
        String endSign = "\n.. Error Codes End\n";
        File file = new File(JavetOSUtils.WORKING_DIRECTORY, "docs/development/error_codes.rst");
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
        int[] maxLengths = Arrays.stream(headerRow).mapToInt(cell -> cell.length()).toArray();
        Map<Integer, String[]> table = new TreeMap<>();
        Class javetErrorClass = JavetError.class;
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
        assertTrue(table.size() > 0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fileContent.substring(0, startPosition));
        String separator = String.join(" ",
                Arrays.stream(maxLengths)
                        .mapToObj(length -> String.join("", Collections.nCopies(length, "=")))
                        .collect(Collectors.toList()));
        stringBuilder.append(separator).append("\n");
        stringBuilder.append(String.join(" ",
                IntStream.range(0, maxLengths.length)
                        .mapToObj(i -> String.format("%1$-" + Integer.toString(maxLengths[i]) + "s", headerRow[i]))
                        .collect(Collectors.toList()))).append("\n");
        stringBuilder.append(separator).append("\n");
        for (String[] row : table.values()) {
            stringBuilder.append(String.join(" ",
                    IntStream.range(0, maxLengths.length)
                            .mapToObj(i -> String.format("%1$-" + Integer.toString(maxLengths[i]) + "s", row[i]))
                            .collect(Collectors.toList()))).append("\n");
        }
        stringBuilder.append(separator).append("\n\n\n");
        stringBuilder.append(fileContent.substring(endPosition));
        byte[] newBuffer = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(originalBuffer, newBuffer)) {
            // Only generate document when content is changed.
            Files.write(file.toPath(), newBuffer, StandardOpenOption.WRITE);
        }
    }
}
