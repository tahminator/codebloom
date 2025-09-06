import type { UserTagTag } from "@/lib/api/types/autogen/schema";

import { SegmentedControl } from "@mantine/core";
import { useMemo } from "react";

/** Segment identifiers used in the UI */
export const SEGMENTS = [
  { label: "All", value: "all" },
  { label: "Patina", value: "patina" },
  { label: "Hunter", value: "hunter" },
  { label: "NYU", value: "nyu" },
  { label: "Baruch", value: "baruch" },
  { label: "RPI", value: "rpi" },
  { label: "GWC", value: "gwc" },
] as const;

type SegmentValue = (typeof SEGMENTS)[number]["value"];
type SegmentKey = Exclude<SegmentValue, "all">;

/** Backend enum → UI segment value */
const TAG_TO_VALUE: Record<UserTagTag, SegmentKey> = {
  Patina: "patina",
  Hunter: "hunter",
  Nyu: "nyu",
  Baruch: "baruch",
  Rpi: "rpi",
  Gwc: "gwc",
};

type SegmentFlags = Record<SegmentKey, boolean>;
type SegmentToggles = Record<SegmentKey, () => void>;

/** Type guards to narrow string → SegmentValue/SegmentKey */
const ALL_VALUES: readonly SegmentValue[] = SEGMENTS.map((s) => s.value);
const FILTERABLE_VALUES: readonly SegmentKey[] = ALL_VALUES.filter(
  (v): v is SegmentKey => v !== "all",
);

function isSegmentKey(v: string): v is SegmentKey {
  return (FILTERABLE_VALUES as readonly string[]).includes(v);
}

export default function FilterCotrol({
  userTags,
  flags,
  toggles,
}: {
  userTags: { tag: UserTagTag }[];
  flags: SegmentFlags;
  toggles: SegmentToggles;
}) {
  const currentValue: SegmentValue =
    flags.patina ? "patina"
    : flags.hunter ? "hunter"
    : flags.nyu ? "nyu"
    : flags.baruch ? "baruch"
    : flags.rpi ? "rpi"
    : flags.gwc ? "gwc"
    : "all";

  // Build allowed segment list from the user's tags
  const allowedValues = useMemo(
    () => new Set<SegmentKey>(userTags.map((t) => TAG_TO_VALUE[t.tag])),
    [userTags],
  );

  const data = useMemo(
    () =>
      SEGMENTS.filter(
        (seg) =>
          seg.value === "all" || allowedValues.has(seg.value as SegmentKey),
      ),
    [allowedValues],
  );

  return (
    <SegmentedControl
      value={currentValue}
      w="100%"
      variant="light"
      data={data}
      // Clicking anywhere while a filter is active will "untoggle" back to All
      onClick={(e) => {
        // Small nitpick fix
        // Will not reset to "all" if the small gaps around the body of the SegmentedControl are clicked
        if (e.target === e.currentTarget) return;

        // Disables the previous filter when another one is selected
        if (currentValue !== "all") {
          toggles[currentValue as SegmentKey]?.();
        }
      }}
      // Mantine passes a string; narrow it before using.
      onChange={(value: string) => {
        if (isSegmentKey(value) && currentValue !== value) {
          toggles[value]?.();
        }
      }}
    />
  );
}
