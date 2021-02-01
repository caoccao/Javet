package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;

@SuppressWarnings("unchecked")
public interface IV8ValueMap extends IV8ValueSet {
    IV8ValueCollection getValues() throws JavetException;
}
