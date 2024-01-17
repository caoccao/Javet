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

package com.caoccao.javet.utils;

import java.util.Map;

/**
 * The type Simple free marker format.
 *
 * @since 0.8.5
 */
public final class SimpleFreeMarkerFormat {
    /**
     * The constant STRING_NULL.
     *
     * @since 0.8.5
     */
    public static final String STRING_NULL = "<null>";
    private static final char CHAR_DOLLAR = '$';
    private static final char CHAR_VARIABLE_CLOSE = '}';
    private static final char CHAR_VARIABLE_OPEN = '{';

    private SimpleFreeMarkerFormat() {
    }

    /**
     * Format string.
     *
     * @param format     the format
     * @param parameters the parameters
     * @return the string
     * @since 0.8.5
     */
    public static String format(final String format, final Map<String, Object> parameters) {
        if (StringUtils.isEmpty(format) || parameters == null || parameters.isEmpty()) {
            return format;
        }
        final int length = format.length();
        StringBuilder stringBuilderMessage = new StringBuilder();
        StringBuilder stringBuilderVariable = new StringBuilder();
        State state = State.Text;
        for (int i = 0; i < length; ++i) {
            final char c = format.charAt(i);
            switch (c) {
                case CHAR_DOLLAR:
                    switch (state) {
                        case Text:
                            state = State.Dollar;
                            break;
                        case Dollar:
                            state = State.Text;
                            stringBuilderMessage.append(CHAR_DOLLAR).append(c);
                            break;
                        case Variable:
                            stringBuilderVariable.append(c);
                            break;
                    }
                    break;
                case CHAR_VARIABLE_OPEN:
                    switch (state) {
                        case Dollar:
                            state = State.Variable;
                            break;
                        case Variable:
                            stringBuilderVariable.append(c);
                            break;
                        default:
                            state = State.Text;
                            stringBuilderMessage.append(c);
                            break;
                    }
                    break;
                case CHAR_VARIABLE_CLOSE:
                    if (state == State.Variable) {
                        String variableName = stringBuilderVariable.toString();
                        Object parameter = parameters.get(variableName);
                        if (parameter == null) {
                            parameter = STRING_NULL;
                        }
                        stringBuilderMessage.append(parameter);
                        stringBuilderVariable.setLength(0);
                        state = State.Text;
                    } else {
                        stringBuilderMessage.append(c);
                    }
                    break;
                default:
                    switch (state) {
                        case Dollar:
                            state = State.Text;
                            stringBuilderMessage.append(CHAR_DOLLAR).append(c);
                            break;
                        case Variable:
                            stringBuilderVariable.append(c);
                            break;
                        default:
                            stringBuilderMessage.append(c);
                            break;
                    }
                    break;
            }
        }
        switch (state) {
            case Dollar:
                stringBuilderMessage.append(CHAR_DOLLAR);
                break;
            case Variable:
                stringBuilderMessage.append(CHAR_DOLLAR).append(CHAR_VARIABLE_OPEN).append(stringBuilderVariable);
                break;
        }
        return stringBuilderMessage.toString();
    }

    /**
     * The enum State.
     *
     * @since 0.8.5
     */
    enum State {
        /**
         * Text state.
         *
         * @since 0.8.5
         */
        Text,
        /**
         * Dollar state.
         *
         * @since 0.8.5
         */
        Dollar,
        /**
         * Variable state.
         *
         * @since 0.8.5
         */
        Variable,
    }
}
