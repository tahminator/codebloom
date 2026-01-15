package org.patinanetwork.codebloom.common.security.annotation;

import java.lang.annotation.*;

/**
 * Can mark a controller method as protected. This is the same as injecting the `Protector` class and calling the
 * methods inside of the controller with the `HttpServletRequest` object. You can find an example below.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @Protected
 * public ResponseEntity<ApiResponder<AuthenticationObjectDto>> validateAuth(final AuthenticationObject authenticationObject) { // @Protected will automatically fill this object if authenticated.
 *     User user = authenticationObject.getUser(); // it is guaranteed to exist at this point
 *     return ResponseEntity.ok().body(ApiResponder.success("You are authenticated!",
 *                     AuthenticationObjectDto.fromAuthenticationObject(authenticationObject)));
 * }
 * }</pre>
 *
 * @see <a href= "https://github.com/tahminator/codebloom/blob/main/docs/backend/auth.md">Authentication
 *     Documentation</a>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Protected {
    /** If set to `false`, will allow any authenticated user. Default: `false`. */
    boolean admin() default false;
}
