package com.netflix.hollow.api.consumer.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field path associated with a field or method declaration whose type or
 * return type respectively is associated resolution of the field path.
 *
 * @see com.netflix.hollow.core.index.FieldPaths
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD, ElementType.METHOD}) public @interface FieldPath {
    /**
     * @return the field path, if empty then the path is derived from the field or method name.
     */
    String value() default "";

    /**
     * @return the field path order
     */
    int order() default 0;
}
