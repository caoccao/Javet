package com.caoccao.javet.interop.node;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.nio.file.Path;

public class NodeModuleModule extends BaseNodeModule {

    public static final String FUNCTION_CREATE_REQUIRE = "createRequire";
    public static final String PROPERTY_REQUIRE = "require";

    public NodeModuleModule(V8ValueObject moduleObject, String name) {
        super(moduleObject, name);
    }

    public void setRequireRootDirectory(Path path) throws JavetException {
        try (V8ValueObject v8ValueObject = moduleObject.invoke(FUNCTION_CREATE_REQUIRE,
                new V8ValueString(path.toAbsolutePath().toString()))) {
            moduleObject.getV8Runtime().getGlobalObject().set(PROPERTY_REQUIRE, v8ValueObject);
        }
    }
}
