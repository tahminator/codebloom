import { components } from "@/lib/api/types/schema";

/**
 * Equivalent to an empty object: {}
 *
 * This is used as ApiResponse<Empty> which
 * signifies that even if the operation was successful,
 * the `payload` key will just be = {}.
 *
 * @example
 * ```json
 * {
 *    success: true,
 *    message: "Operation was successful!",
 *    payload: {}
 * }
 * ```
 */
export type Empty = components["schemas"]["Empty"];
