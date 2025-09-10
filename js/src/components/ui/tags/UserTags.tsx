import { UserTag, UserTagTagWithoutGwc } from "@/lib/api/types/user";
export type { UserTag };

export const TAG_ICONS_LIST: Record<
  UserTagTagWithoutGwc,
  {
    name: string;
    icon: string;
    alt: string;
  }
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
    icon: "", 
    alt: "Patina Logo",
  },
} as const;
