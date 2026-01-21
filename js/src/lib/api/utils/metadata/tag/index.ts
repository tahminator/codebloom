import { Tag } from "@/lib/api/types/schema";
import { ApiTypeUtils } from "@/lib/api/utils/types";

/**
 * Metadata for all Tag enums.
 */
export const TAG_METADATA_LIST: Record<Tag, ApiTypeUtils.TagMetadata> = {
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
    icon: "/brands/BMCC_logo.png",
    alt: "BMCC Logo",
  },
} as const;

export const UNUSED_TAGS: Tag[] = [Tag.Gwc];
