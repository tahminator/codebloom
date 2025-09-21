import { UserTag, UserTagTag } from "@/lib/api/types/usertag";
import { ApiTypeUtils } from "@/lib/api/utils/types";

/**
 * A collection of helpful utilities to help transform & use data returned from the API.
 */
export class ApiUtils {
  private static readonly _TAG_METADATA_LIST: Record<
    UserTagTag,
    ApiTypeUtils.UserTagTagMetadata
  > = {
    Hunter: {
      name: "Hunter College",
      icon: "/Hunter_Logo.jpeg",
      alt: "Hunter College Logo",
    },
    Nyu: {
      name: "NYU",
      icon: "/NYU_Logo.png",
      alt: "NYU Logo",
    },
    Baruch: {
      name: "Baruch College",
      icon: "/Baruch_Logo.png",
      alt: "Baruch College Logo",
    },
    Rpi: {
      name: "RPI",
      icon: "/Rpi_Logo.png",
      alt: "RPI Logo",
    },
    Patina: {
      name: "Patina",
      icon: "/Patina_Logo.png",
      alt: "Patina Logo",
    },
    Gwc: {
      name: "Hunter GWC",
      icon: "/Gwc_Logo.png",
      alt: "GWC Logo",
    },
  } as const;

  static _UNUSED_TAGS: UserTagTag[] = [UserTagTag.Gwc];

  private static _isSupportedTag(
    tag: UserTag,
  ): tag is ApiTypeUtils.FilteredUserTag {
    return !ApiUtils._UNUSED_TAGS.includes(tag.tag);
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
   * Given a list of tags, strip the tags that contains a `tag`
   * enum that we do not actually support directly on our frontend.
   *
   * @see {@link _UNUSED_TAGS}
   */
  static filterUnusedTags(tags: UserTag[]): ApiTypeUtils.FilteredUserTag[] {
    return tags.filter(ApiUtils._isSupportedTag);
  }
}
