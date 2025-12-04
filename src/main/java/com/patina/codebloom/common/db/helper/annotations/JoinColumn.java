package com.patina.codebloom.common.db.helper.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark database fields that are not part of the table; rather, they are joined from another table (likely
 * using some form of `id`).
 *
 * <p>Used at compile-time to indicate that the annotated field corresponds to a database column that cannot contain
 * null values.
 *
 * <p>This interface will get compiled out; there is no run-time effects from this annotation.
 */
@Target({FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface JoinColumn {}
