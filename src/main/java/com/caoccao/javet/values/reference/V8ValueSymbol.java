package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.text.MessageFormat;

public class V8ValueSymbol extends V8ValueObject {

    public static final String DESCRIPTION = "description";
    public static final String SYMBOL_0 = "Symbol({0})";

    public V8ValueSymbol(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Symbol;
    }

    public String getDescription() throws JavetException {
        return getPropertyString(DESCRIPTION);
    }

    @Override
    public String toString() {
        try {
            return MessageFormat.format(SYMBOL_0, getDescription());
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
