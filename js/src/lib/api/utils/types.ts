import { UserTag, UserTagTag } from "@/lib/api/types/usertag";

/**
 * A collection of helpful types to help transform & use data returned from the API.
 */
export namespace ApiTypeUtils {
  export type UserTagTagMetadata = {
    /**
     * Pretty short name for the given tag.
     */
    shortName: string;

    /**
     * Pretty name for the given tag.
     */
    name: string;

    /**
     * Path to tag's icon.
     */
    icon: string;

    /**
     * Extra information about the given tag.
     */
    alt: string;
  };

  /**
   * The `tag` enum inside of `UserTag`, but stripped of all unsupported tags.
   *
   * @see {@link UserTagTag}
   * @see {@link FilteredUserTag}
   */
  export type FilteredUserTagTag = Exclude<UserTagTag, UserTagTag.Gwc>;

  /**
   * Type override of the `UserTag` object, but stripped of all unsupported `tag` enums.
   *
   * @see {@link UserTag}
   * @see {@link FilteredUserTagTag}
   */
  export type FilteredUserTag = UserTag & {
    tag: FilteredUserTagTag;
  };
}
