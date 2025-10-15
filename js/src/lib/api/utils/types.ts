import { UserTagTag } from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";

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
     * How the backend expects to receive this tag when passed in as a URL param.
     *
     * @note This is currently implemented as an all lowercase version of the enum, but
     * THIS CAN CHANGE. SO DO NOT DO THE LOGIC OF MAKING THE API KEY YOURSELF.
     *
     * Instead, do this:
     *
     * @example
     * ```ts
     * const metadata = ApiUtils.getMetadataByTagEnum(UserTagTag.Gwc);
     *
     * const apiKey = metadata.apiKey; // use this.
     *
     * const fetch = (`api/blah/blah?${apiKey}=false`);
     * ```
     */
    apiKey: Lowercase<UserTagTag>;

    /**
     * Extra information about the given tag.
     */
    alt: string;
  };

  /**
   * The `tag` enum inside of `UserTag`, but stripped of all unsupported tags.
   *
   * @note - This is just a subset of `UserTagTag`. As such, you can pass a `FilteredUserTagTag`
   * into any function that accepts `UserTagTag`.
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

  /**
   * Pretty name for the given topic.
   */
  export type QuestionTopicTopicMetadata = {
    name: string;
  };
}
