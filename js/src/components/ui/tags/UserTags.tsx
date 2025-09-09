export type TagType = "Patina" | "Hunter" | "Nyu" | "Baruch" | "Rpi" | "Gwc";

export interface UserTag {
  id: string;
  createdAt: string;
  userId: string;
  tag: TagType;
}

export const School_List = {
  Hunter: {
    name: "Hunter College",
    icon: "/Hunter_Logo.jpeg",
    alt: "Hunter College Logo"
  },
  Nyu: {
    name: "NYU",
    icon: "/NYU_Logo.png",
    alt: "NYU Logo"
  },
  Baruch: {
    name: "Baruch College",
    icon: "/Baruch_Logo.png",
    alt: "Baruch College Logo"
  },
  Rpi: {
    name: "RPI",
    icon: "/Rpi_Logo.png",
    alt: "RPI Logo"
  }
} as const;