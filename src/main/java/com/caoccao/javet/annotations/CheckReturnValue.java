package com.caoccao.javet.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckReturnValue {
}
