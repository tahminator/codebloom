import { QuestionTopicTopic, UserTagTag } from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
import { ApiTypeUtils } from "@/lib/api/utils/types";

import { TAG_METADATA_LIST, UNUSED_TAGS } from "./metadata/tag";
import { TOPIC_METADATA_LIST } from "./metadata/topic";

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
   * @returns {string} metadata.name - Name to use when actually showcasing the tag on the frontend
   * @returns {string} metadata.icon - Path to image of tag enum. You can pass this directly to anything that loads an image.
   * @returns {string} metadata.alt - A description of the icon/tag.
   */
  static getMetadataByTagEnum(
    tagEnum: UserTagTag,
  ): ApiTypeUtils.UserTagTagMetadata {
    return ApiUtils._TAG_METADATA_LIST[tagEnum];
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
   * Return a list of all topic enums. Essentially a shorthand for:
   * ```ts
   * Object.values(QuestionTopicTopic);
   * ```
   */
  static getAllTopicEntries(): Record<
    QuestionTopicTopic,
    ApiTypeUtils.QuestionTopicTopicMetadata
  > {
    return this._TOPIC_METADATA_LIST;
  }

  /**
   * Return a list of all supported tag enums. Essentially a shorthand for:
   * ```ts
   * Object.values(UserTagTag).filter(ApiUtils._isSupportedTagEnum);
   * ```
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
}
