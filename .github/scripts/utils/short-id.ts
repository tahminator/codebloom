/**
 * Generate a short ID (default len = 7).
 *
 * @note THIS DOES NOT GUARANTEE EXTREMELY LOW COLLISIONS.
 */
export function generateShortId(len = 7) {
  return Math.random()
    .toString(36)
    .substring(2, 2 + len);
}
