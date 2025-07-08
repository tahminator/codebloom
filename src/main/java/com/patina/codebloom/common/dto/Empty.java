package com.patina.codebloom.common.dto;

/**
 * Use {@code Empty.of()} when you want to return a successful response, but
 * have no data to send back to the client.
 *
 * The client will only see an empty object, which is more ideal than an
 * undefined key.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * public ResponseEntity<ApiResponder<Empty>> exampleFunction() {
 *     // ...
 *     return ResponseEntity.ok(ApiResponder.success(Empty.of())); // returns an empty object: {}
 * }
 * }</pre>
 */
public final class Empty {
    private static final Empty INSTANCE = new Empty();

    private Empty() {
    }

    /**
     * Use {@code Empty.of()} when you want to return a successful response, but
     * have no data to send back to the client.
     *
     * The client will only see an empty object, which is more ideal than an
     * undefined key.
     *
     * <p>
     * Example usage:
     *
     * <pre>{@code
     * public ResponseEntity<ApiResponder<Empty>> exampleFunction() {
     *     // ...
     *     return ResponseEntity.ok(ApiResponder.success(Empty.of())); // returns an empty object: {}
     * }
     * }</pre>
     */
    public static Empty of() {
        return INSTANCE;
    }
}
