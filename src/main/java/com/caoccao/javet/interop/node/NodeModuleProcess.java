package com.caoccao.javet.interop.node;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.File;
import java.nio.file.Path;

@NodeModuleAnnotation(name = "process")
public class NodeModuleProcess extends BaseNodeModule {
    public static final String FUNCTION_CHDIR = "chdir";
    public static final String FUNCTION_CWD = "cwd";

    public NodeModuleProcess(V8ValueObject moduleObject, String name) {
        super(moduleObject, name);
    }

    public Path getWorkingDirectory() throws JavetException {
        return new File(moduleObject.invokeString(FUNCTION_CWD)).toPath();
    }

    public void setWorkingDirectory(Path path) throws JavetException {
        moduleObject.invokeVoid(FUNCTION_CHDIR, new V8ValueString(path.toAbsolutePath().toString()));
    }
}
