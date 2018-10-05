package com.netflix.hollow.core.write.objectmapper;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD})
public @interface DeprecatedApi {
    String value() default "Empty Javadoc";
}
