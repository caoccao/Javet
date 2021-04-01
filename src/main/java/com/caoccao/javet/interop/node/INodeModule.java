package com.caoccao.javet.interop.node;

import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.values.reference.V8ValueObject;

public interface INodeModule extends IJavetClosable {
    V8ValueObject getModuleObject();

    String getName();
}
