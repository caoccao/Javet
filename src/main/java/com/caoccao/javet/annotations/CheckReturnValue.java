package com.caoccao.javet.annotations;

import java.lang.annotation.*;

/**
 * This annotation is for IDE to warn application developers to consume the return value.
 * Memory leak may occur if the return value is not consumed.
 *
 * @since 0.8.10
 */
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckReturnValue {
}
