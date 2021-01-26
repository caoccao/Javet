package com.caoccao.javet;

import com.caoccao.javet.interop.V8Host;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTestJavet {
    @BeforeAll
    public static void beforeAll() {
        V8Host.getInstance().setFlags("--use_strict");
    }
}
