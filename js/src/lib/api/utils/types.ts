import type { Api } from "@/lib/api/types";

import {
  TagMetadataObject,
  TopicMetadataObject,
} from "@/lib/api/types/complex";
import { Tag } from "@/lib/api/types/schema";
import { UNUSED_TAGS } from "@/lib/api/utils/metadata/tag";

/**
 * A collection of helpful types to help transform & use data returned from the API.
 */
export namespace ApiTypeUtils {
  export type TagMetadata = TagMetadataObject & {
    /**
     * Path to tag's icon.
     */
    icon: string;
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
  export type FilteredTag = Exclude<
    Tag,
    (typeof UNUSED_TAGS)[keyof typeof UNUSED_TAGS]
  >;

  /**
   * Type override of the `UserTag` object, but stripped of all unsupported `tag` enums.
   *
   * @see {@link UserTag}
   * @see {@link FilteredTag}
   */
  export type FilteredUserTag = Api<"UserTag"> & {
    tag: FilteredTag;
  };

  /**
   * Pretty name for the given topic.
   */
  export type QuestionTopicTopicMetadata = TopicMetadataObject;
}
