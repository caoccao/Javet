package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueMap;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueSet;

public interface IV8Creatable {
    V8ValueArray createV8ValueArray() throws JavetException;

    V8ValueMap createV8ValueMap() throws JavetException;

    V8ValueObject createV8ValueObject() throws JavetException;

    V8ValueSet createV8ValueSet() throws JavetException;
}
