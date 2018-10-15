package com.netflix.hollow.core.write.objectmapper;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is meant to be used on data model fields to point
 * deprecation and discourage to use them.
 *
 * <p>An annotated field with {@code @DeprecatedApi} will be transformed during
 * Consumer API Generation into snippets consisting of JavaDoc information,
 * if any exists, and {@link java.lang.Deprecated} annotation on accessor method.
 *
 * <p>Example:
 *
 * <p>The following data model snippet:
 *
 * <pre>
 *  {@literal @DeprecatedApi}("Use {{@literal @link} #getNewValue()} instead.")
 *  private final long value;
 *
 *  private final long newValue;
 *
 *  {@literal @DeprecatedApi}
 *  private final String name;
 * </pre>
 *
 * <p>will be transformed into:
 *
 * <pre>
 *  /**
 *   * @deprecated Use {{@literal @link} #getNewValue()} instead.
 *   * /
 *  {@literal @Deprecated}
 *  public final long getValue(){...}
 *
 *  public final long getNewValue(){...}
 *
 *  {@literal @Deprecated}
 *  public final String getName(){...}
 * </pre>
 *
 * @see java.lang.Deprecated
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface DeprecatedApi {
    String value() default "";
}
