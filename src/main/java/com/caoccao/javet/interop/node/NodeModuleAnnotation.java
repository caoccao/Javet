package com.caoccao.javet.interop.node;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NodeModuleAnnotation {
    String name() default "";
}
