package com.patina.codebloom.common.db.helper.annotations;

import java.lang.annotation.Target;
// CHECKSTYLE:OFF
import static java.lang.annotation.ElementType.*;
// CHECKSTYLE:ON

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark database fields as NULL columns.
 * 
 * Used at compile-time to indicate that the annotated field corresponds to a
 * database column that can contain null values (i.e., is nullable).
 *
 * This interface will get compiled out; there is no run-time effects from this
 * annotation.
 */
@Target({ FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface NullColumn {

}
