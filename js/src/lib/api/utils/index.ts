import { LeetcodeTopicEnum, Tag } from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
import { TAG_METADATA_LIST, UNUSED_TAGS } from "@/lib/api/utils/metadata/tag";
import { TOPIC_METADATA_LIST } from "@/lib/api/utils/metadata/topic";
import { ApiTypeUtils } from "@/lib/api/utils/types";

/**
 * A collection of helpful utilities to help transform & use data returned from the API.
 */
export class ApiUtils {
  private static readonly _TAG_METADATA_LIST = TAG_METADATA_LIST;

  static _UNUSED_TAGS = UNUSED_TAGS;

  private static readonly _TOPIC_METADATA_LIST = TOPIC_METADATA_LIST;

  private static _isSupportedTag(
    tag: UserTag,
  ): tag is ApiTypeUtils.FilteredUserTag {
    return !ApiUtils._UNUSED_TAGS.includes(tag.tag);
  }

  private static _isSupportedTagEnum(
    tagEnum: Tag,
  ): tagEnum is ApiTypeUtils.FilteredTag {
    return !ApiUtils._UNUSED_TAGS.includes(tagEnum);
  }

  /**
   * Receive @type {ApiTypeUtils.TagMetadata} from a `tagEnum`.
   *
   * @returns {ApiTypeUtils.TagMetadata} metadata - Metadata object
   */
  static getMetadataByTagEnum(tagEnum: Tag): ApiTypeUtils.TagMetadata {
    return ApiUtils._TAG_METADATA_LIST[tagEnum];
  }

  /**
   * Convenience method to retrieve tag metadata for a leaderboard value.
   * Both the input and lookup are of type `Tag`; this method exists for semantic clarity
   * when working with leaderboard contexts, and may help future extensibility.
   *
   * @returns {ApiTypeUtils.TagMetadata} metadata - Metadata object
   */
  static getTagMetadataFromLeaderboard(
    leaderboard: Tag,
  ): ApiTypeUtils.TagMetadata {
    return ApiUtils.getMetadataByTagEnum(leaderboard);
  }

  /**
   * Receive @type {ApiTypeUtils.QuestionTopicTopicMetadata} from a `topicEnum`.
   *
   * @returns {ApiTypeUtils.QuestionTopicTopicMetadata} metadata - Metadata object
   */
  static getTopicEnumMetadataByTopicEnum(
    topicEnum: LeetcodeTopicEnum,
  ): ApiTypeUtils.QuestionTopicTopicMetadata {
    return ApiUtils._TOPIC_METADATA_LIST[topicEnum];
  }

  /**
   * Returns a list of all metadata objects that exist on a given tag, which are iterable.
   *
   * @note - This list is always consistently ordered, as guaranteed by {@link Object.entries}
   */
  static getAllTagEnumMetadata(): ApiTypeUtils.TagMetadata[] {
    return Object.typedEntries(ApiUtils._TAG_METADATA_LIST).map(
      ([_, metadata]) => metadata,
    );
  }

  /**
   * Returns a list of all metadata objects that exist on a given tag, which are iterable.
   *
   * @note - This list is always consistently ordered, as guaranteed by {@link Object.entries}
   */
  static getAllTopicEnumMetadata(): ApiTypeUtils.QuestionTopicTopicMetadata[] {
    return Object.typedEntries(ApiUtils._TOPIC_METADATA_LIST).map(
      ([_, metadata]) => metadata,
    );
  }

  /**
   * Returns a list of all metadata objects that exist on a given tag & **are supported**, which are iterable.
   *
   * @note - This list is always consistently ordered, as guaranteed by {@link Object.entries}
   */
  static getAllSupportedTagEnumMetadata(): ApiTypeUtils.TagMetadata[] {
    return Object.typedEntries(ApiUtils._TAG_METADATA_LIST)
      .filter(([tagEnum, _]) => this._isSupportedTagEnum(tagEnum))
      .map(([_, metadata]) => metadata);
  }

  /**
   * Return a list of all tag enums. Essentially a shorthand for:
   * ```ts
   * Object.values(Tag);
   * ```
   */
  static getAllTagEnums(): Tag[] {
    return Object.values(Tag);
  }

  /**
   * Returns a list of all topic enums. Essentially a shorthand for:
   * ```ts
   * Object.values(QuestionTopicTopic);
   * ```
   */
  static getAllTopicEntries(): Record<
    LeetcodeTopicEnum,
    ApiTypeUtils.QuestionTopicTopicMetadata
  > {
    return this._TOPIC_METADATA_LIST;
  }

  /**
   * Returns a mapping of all topic enums to their metadata.
   * Essentially a shorthand for:
   * ```ts
   * ApiUtils._TOPIC_METADATA_LIST
   */
  static getAllSupportedTagEnums(): ApiTypeUtils.FilteredTag[] {
    return Object.values(Tag).filter(ApiUtils._isSupportedTagEnum);
  }

  /**
   * Given a list of tags, strip the tags that contains a `tag`
   * enum that we do not actually support directly on our frontend.
   *
   * @see {@link _UNUSED_TAGS}
   */
  static filterUnusedTags(tags: UserTag[]): ApiTypeUtils.FilteredUserTag[] {
    return tags.filter(ApiUtils._isSupportedTag);
  }

  static filterUnusedTagEnums(tagEnums: Tag[]): ApiTypeUtils.FilteredTag[] {
    return tagEnums.filter(ApiUtils._isSupportedTagEnum);
  }

  static matchTopic(
    topic: ApiTypeUtils.QuestionTopicTopicMetadata,
    query: string,
  ) {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) return true;
    const allMatches = [topic.name, ...(topic.aliases ?? [])].map((s) =>
      s.toLowerCase(),
    );
    return allMatches.some(
      (term) => term.includes(lowerQuery) || lowerQuery.includes(term),
    );
  }
}

export function getTagMetadataFromLeaderboard(leaderboard: Tag) {
  return ApiUtils.getTagMetadataFromLeaderboard(leaderboard);
}
