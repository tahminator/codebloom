package com.patina.codebloom.common.utils.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to log the execution time of a method.
 *
 * <p>When applied to a method, logs the execution time to SLF4J by default.
 * Optionally, if {@code reportToDiscord} is set to {@code true}, the execution
 * time will also be reported to Discord for monitoring purposes.
 *
 *
 * <b>Example usage:</b>
 * <pre>{@code
 * @LogExecutionTime(reportToDiscord = true)
 * public void processData() {
 *     // method implementation
 * }
 * }</pre>
 *
 * @apiNote <b>Performance implications:</b> Reporting to Discord is expensive, so <b>use it sparingly</b>.
 *
 * @see LogExecutionTimeAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    boolean reportToDiscord() default false;
}
