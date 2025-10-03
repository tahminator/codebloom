import { components } from "@/lib/api/types/autogen/schema";

type Schemas = components["schemas"];

export type TApiSchemaKey = keyof Schemas;

/**
 * Select an API type. Check example below on how to use `Api`.
 *
 * @example
 * ```ts
 * // old way
 * const json = (await response.json()) as UnknownApiResponse<UserDto>; // UserDto must be exported manually.
 *
 * // new way
 * const json = (await response.json()) as UnknownApiResponse<Api<"UserDto">>; // Access any generated API type.
 * ```
 */
export type Api<T extends TApiSchemaKey> = components["schemas"][T];
