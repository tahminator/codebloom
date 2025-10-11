import { UserTagTag } from "@/lib/api/types/autogen/schema";
import { UserTag } from "@/lib/api/types/usertag";
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
      shortName: "Hunter",
      name: "Hunter College",
      apiKey: "hunter",
      icon: "/brands/Hunter_Logo.jpeg",
      alt: "Hunter College Logo",
    },
    Nyu: {
      shortName: "NYU",
      name: "New York University",
      apiKey: "nyu",
      icon: "/brands/NYU_Logo.png",
      alt: "NYU Logo",
    },
    Baruch: {
      shortName: "Baruch",
      name: "Baruch College",
      apiKey: "baruch",
      icon: "/brands/Baruch_Logo.png",
      alt: "Baruch College Logo",
    },
    Rpi: {
      shortName: "RPI",
      name: "Rensselaer Polytechnic Institute",
      apiKey: "rpi",
      icon: "/brands/Rpi_Logo.png",
      alt: "RPI Logo",
    },
    Patina: {
      shortName: "Patina",
      name: "Patina Network",
      apiKey: "patina",
      icon: "/brands/Patina_Logo.png",
      alt: "Patina Logo",
    },
    Gwc: {
      shortName: "GWC @ Hunter",
      name: "Hunter College - GWC",
      apiKey: "gwc",
      icon: "/brands/Gwc_Logo.png",
      alt: "GWC Logo",
    },
    Sbu: {
      shortName: "SBU",
      name: "Stony Brook University",
      apiKey: "sbu",
      icon: "/brands/SBU_shield.png",
      alt: "Stony Brook University Logo",
    },
    Columbia: {
      shortName: "Columbia",
      name: "Columbia University",
      apiKey: "columbia",
      icon: "/brands/Columbia_logo.png",
      alt: "Columbia University Logo",
    },
    Ccny: {
      shortName: "CCNY",
      name: "City College of New York",
      apiKey: "ccny",
      icon: "/brands/CCNY_logo.png",
      alt: "City College of New York Logo",
    },
    Cornell: {
      shortName: "Cornell",
      name: "Cornell University",
      apiKey: "cornell",
      icon: "/brands/Cornell_Logo.png",
      alt: "Cornell University Logo",
    },
    Bmcc: {
      shortName: "BMCC",
      name: "Borough of Manhattan Community College",
      apiKey: "bmcc",
      icon: "/brands/BMCC_Logo.png",
      alt: "BMCC Logo",
    },
  } as const;

  static _UNUSED_TAGS: UserTagTag[] = [UserTagTag.Gwc];

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
