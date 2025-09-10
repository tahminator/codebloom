import { components , UserTagTag } from "@/lib/api/types/autogen/schema";
export type User = components["schemas"]["User"];
export type PrivateUser = components["schemas"]["PrivateUser"];
export type UserWithScore = components["schemas"]["UserWithScore"];
export type UserTag = components["schemas"]["UserTag"];


export { UserTagTag };

export type UserTagTagWithoutGwc = Exclude<UserTagTag, UserTagTag.Gwc>;
