import { TAG_METADATA_LIST as GENERATED_TAG_METADATA_LIST } from "@/lib/api/types/complex";
import { Tag } from "@/lib/api/types/schema";
import { ApiTypeUtils } from "@/lib/api/utils/types";

const TAG_ICONS: Record<Tag, string> = {
  [Tag.Patina]: "/brands/Patina_Logo.png",
  [Tag.Hunter]: "/brands/Hunter_Logo.jpeg",
  [Tag.Nyu]: "/brands/NYU_Logo.png",
  [Tag.Baruch]: "/brands/Baruch_Logo.png",
  [Tag.Rpi]: "/brands/Rpi_Logo.png",
  [Tag.Gwc]: "/brands/Gwc_Logo.png",
  [Tag.Sbu]: "/brands/SBU_shield.png",
  [Tag.Columbia]: "/brands/Columbia_logo.png",
  [Tag.Ccny]: "/brands/CCNY_logo.png",
  [Tag.Cornell]: "/brands/Cornell_Logo.png",
  [Tag.Bmcc]: "/brands/BMCC_logo.png",
  [Tag.MHCPlusPlus]: "/brands/Mhcpp_logo.png",
};

/**
 * Metadata for all Tag enums.
 */
export const TAG_METADATA_LIST = Object.fromEntries(
  (Object.keys(GENERATED_TAG_METADATA_LIST) as Tag[]).map((tag) => [
    tag,
    { ...GENERATED_TAG_METADATA_LIST[tag], icon: TAG_ICONS[tag] },
  ]),
) as Record<Tag, ApiTypeUtils.TagMetadata>;

export const UNUSED_TAGS = [Tag.Gwc, Tag.MHCPlusPlus] as const;
export const NON_SCHOOL_TAGS = [
  ...UNUSED_TAGS,
  Tag.Patina,
  Tag.MHCPlusPlus,
  Tag.Gwc,
] as const;
