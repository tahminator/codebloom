import { components } from "@/lib/api/types/autogen/schema";

type Schemas = components["schemas"];

export type TApiSchemaKey = keyof Schemas;

export type Api<T extends TApiSchemaKey> = components["schemas"][T];
