import { Tag } from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";

/**
 * A collection of helpful types to help transform & use data returned from the API.
 */
export namespace ApiTypeUtils {
  export type TagMetadata = {
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
     * const metadata = ApiUtils.getMetadataByTagEnum(Tag.Gwc);
     *
     * const apiKey = metadata.apiKey; // use this.
     *
     * const fetch = (`api/blah/blah?${apiKey}=false`);
     * ```
     */
    apiKey: Lowercase<Tag>;

    /**
     * Extra information about the given tag.
     */
    alt: string;
  };

  /**
   * The `tag` enum inside of `UserTag`, but stripped of all unsupported tags.
   *
   * @note - This is just a subset of `Tag`. As such, you can pass a `FilteredTag`
   * into any function that accepts `Tag`.
   *
   * @see {@link Tag}
   * @see {@link FilteredUserTag}
   */
  export type FilteredTag = Exclude<Tag, Tag.Gwc>;

  /**
   * Type override of the `UserTag` object, but stripped of all unsupported `tag` enums.
   *
   * @see {@link UserTag}
   * @see {@link FilteredTag}
   */
  export type FilteredUserTag = UserTag & {
    tag: FilteredTag;
  };

  /**
   * Pretty name for the given topic.
   */
  export type QuestionTopicTopicMetadata = {
    name: string;
    aliases?: string[];
  };
}
