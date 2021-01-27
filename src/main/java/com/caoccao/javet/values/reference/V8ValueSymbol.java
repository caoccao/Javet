package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueSymbol extends V8ValueObject {

    public static final String DESCRIPTION = "description";

    public V8ValueSymbol(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Symbol;
    }

    public String getDescription() throws JavetException {
        return getValueString(DESCRIPTION);
    }
}
