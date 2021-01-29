package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;

@SuppressWarnings("unchecked")
public interface IV8ValueSet extends IV8ValueObject {
    IV8ValueCollection getKeys() throws JavetException;

    int getSize() throws JavetException;

    boolean has(int value) throws JavetException;

    boolean has(String value) throws JavetException;
}
