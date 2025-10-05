import { UserTagTag } from "@/lib/api/types/usertag";
import { ApiTypeUtils } from "@/lib/api/utils/types";

/**
 * Metadata for all UserTagTag enums.
 */
export const TAG_METADATA_LIST: Record<
  UserTagTag,
  ApiTypeUtils.UserTagTagMetadata
> = {
  Hunter: {
    shortName: "Hunter",
    name: "Hunter College",
    icon: "/brands/Hunter_Logo.jpeg",
    alt: "Hunter College Logo",
  },
  Nyu: {
    shortName: "NYU",
    name: "New York University",
    icon: "/brands/NYU_Logo.png",
    alt: "NYU Logo",
  },
  Baruch: {
    shortName: "Baruch",
    name: "Baruch College",
    icon: "/brands/Baruch_Logo.png",
    alt: "Baruch College Logo",
  },
  Rpi: {
    shortName: "RPI",
    name: "Rensselaer Polytechnic Institute",
    icon: "/brands/Rpi_Logo.png",
    alt: "RPI Logo",
  },
  Patina: {
    shortName: "Patina",
    name: "Patina Network",
    icon: "/brands/Patina_Logo.png",
    alt: "Patina Logo",
  },
  Gwc: {
    shortName: "GWC@Hunter",
    name: "Hunter College - GWC",
    icon: "/brands/Gwc_Logo.png",
    alt: "GWC Logo",
  },
  Sbu: {
    shortName: "SBU",
    name: "Stony Brook University",
    icon: "/brands/SBU_shield.png",
    alt: "Stony Brook University Logo",
  },
  Columbia: {
    shortName: "Columbia",
    name: "Columbia University",
    icon: "/brands/Columbia_logo.png",
    alt: "Columbia University Logo",
  },
  Ccny: {
    shortName: "CCNY",
    name: "City College of New York",
    icon: "/brands/CCNY_logo.png",
    alt: "City College of New York Logo",
  },
  Cornell: {
    shortName: "Cornell",
    name: "Cornell University",
    icon: "/brands/Cornell_Logo.png",
    alt: "Cornell University Logo",
  },
} as const;

export const UNUSED_TAGS: UserTagTag[] = [UserTagTag.Gwc];
