package org.patinanetwork.codebloom.shared;

/**
 * Represents an object whose TypeScript type definition can be exported.
 *
 * <p>Implementing classes should return a valid TypeScript type declaration string from {@link #getTsType()}. The
 * returned string should:
 *
 * <ul>
 *   <li>Be a complete {@code export type ...} declaration
 *   <li>End with {@code \n\n} for consistent spacing in generated output
 *   <li>Use {@link String#stripIndent()} to normalize indentation from text blocks
 * </ul>
 */
public interface TypableObject {
    String tsType();
}
