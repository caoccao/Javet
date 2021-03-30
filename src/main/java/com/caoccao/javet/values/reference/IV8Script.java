package com.caoccao.javet.values.reference;

import com.caoccao.javet.interop.IV8Executable;

@SuppressWarnings("unchecked")
public interface IV8Script extends IV8ValueReference, IV8Executable {
    String getResourceName();

    void setResourceName(String resourceName);
}
