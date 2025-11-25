import {
  QuestionTopicDtoTopic,
  UserTagTag,
  AchievementDtoLeaderboard,
} from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiTypeUtils } from "@/lib/api/utils/types";

import { TAG_METADATA_LIST, UNUSED_TAGS } from "./metadata/tag";
import { TOPIC_METADATA_LIST } from "./metadata/topic";

const LEADERBOARD_TO_TAG: Record<AchievementDtoLeaderboard, UserTagTag> = {
  [AchievementDtoLeaderboard.Hunter]: UserTagTag.Hunter,
  [AchievementDtoLeaderboard.Patina]: UserTagTag.Patina,
  [AchievementDtoLeaderboard.Nyu]: UserTagTag.Nyu,
  [AchievementDtoLeaderboard.Baruch]: UserTagTag.Baruch,
  [AchievementDtoLeaderboard.Rpi]: UserTagTag.Rpi,
  [AchievementDtoLeaderboard.Sbu]: UserTagTag.Sbu,
  [AchievementDtoLeaderboard.Columbia]: UserTagTag.Columbia,
  [AchievementDtoLeaderboard.Ccny]: UserTagTag.Ccny,
  [AchievementDtoLeaderboard.Cornell]: UserTagTag.Cornell,
  [AchievementDtoLeaderboard.Bmcc]: UserTagTag.Bmcc,
  [AchievementDtoLeaderboard.Gwc]: UserTagTag.Gwc,
};

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
    tagEnum: UserTagTag,
  ): tagEnum is ApiTypeUtils.FilteredUserTagTag {
    return !ApiUtils._UNUSED_TAGS.includes(tagEnum);
  }

  /**
   * Receive @type {ApiTypeUtils.UserTagTagMetadata} from a `tagEnum`.
   *
   * @returns {ApiTypeUtils.UserTagTagMetadata} metadata - Metadata object
   */
  static getMetadataByTagEnum(
    tagEnum: UserTagTag,
  ): ApiTypeUtils.UserTagTagMetadata {
    return ApiUtils._TAG_METADATA_LIST[tagEnum];
  }

  /**
   * Receive @type {ApiTypeUtils.UserTagTagMetadata} from a `leaderboard` enum.
   * Maps a leaderboard enum value to its corresponding tag enum.
   *
   * @returns {ApiTypeUtils.UserTagTagMetadata} metadata - Metadata object
   */
  static getTagMetadataFromLeaderboard(
    leaderboard: AchievementDtoLeaderboard,
  ): ApiTypeUtils.UserTagTagMetadata {
    const tagEnum = LEADERBOARD_TO_TAG[leaderboard];
    return ApiUtils.getMetadataByTagEnum(tagEnum);
  }

  /**
   * Receive @type {ApiTypeUtils.QuestionTopicTopicMetadata} from a `topicEnum`.
   *
   * @returns {ApiTypeUtils.QuestionTopicTopicMetadata} metadata - Metadata object
   */
  static getTopicEnumMetadataByTopicEnum(
    topicEnum: QuestionTopicDtoTopic,
  ): ApiTypeUtils.QuestionTopicTopicMetadata {
    return ApiUtils._TOPIC_METADATA_LIST[topicEnum];
  }

  /**
   * Returns a list of all metadata objects that exist on a given tag, which are iterable.
   *
   * @note - This list is always consistently ordered, as guaranteed by {@link Object.entries}
   */
  static getAllTagEnumMetadata(): ApiTypeUtils.UserTagTagMetadata[] {
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
  static getAllSupportedTagEnumMetadata(): ApiTypeUtils.UserTagTagMetadata[] {
    return Object.typedEntries(ApiUtils._TAG_METADATA_LIST)
      .filter(([tagEnum, _]) => this._isSupportedTagEnum(tagEnum))
      .map(([_, metadata]) => metadata);
  }

  /**
   * Return a list of all tag enums. Essentially a shorthand for:
   * ```ts
   * Object.values(UserTagTag);
   * ```
   */
  static getAllTagEnums(): UserTagTag[] {
    return Object.values(UserTagTag);
  }

  /**
   * Returns a list of all topic enums. Essentially a shorthand for:
   * ```ts
   * Object.values(QuestionTopicTopic);
   * ```
   */
  static getAllTopicEntries(): Record<
    QuestionTopicDtoTopic,
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
  static getAllSupportedTagEnums(): ApiTypeUtils.FilteredUserTagTag[] {
    return Object.values(UserTagTag).filter(ApiUtils._isSupportedTagEnum);
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

  static filterUnusedTagEnums(
    tagEnums: UserTagTag[],
  ): ApiTypeUtils.FilteredUserTagTag[] {
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

export function getTagMetadataFromLeaderboard(
  leaderboard: AchievementDtoLeaderboard,
) {
  const tagEnum = LEADERBOARD_TO_TAG[leaderboard];
  return ApiUtils.getMetadataByTagEnum(tagEnum);
}
