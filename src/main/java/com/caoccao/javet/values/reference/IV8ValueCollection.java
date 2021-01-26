package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.exceptions.JavetV8RuntimeNotRegisteredException;

public interface IV8ValueCollection extends IV8ValueReference {
    int getLength() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeNotRegisteredException;
}
