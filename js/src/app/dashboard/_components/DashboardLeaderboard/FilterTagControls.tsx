import { UserTagTag } from "@/lib/api/types/usertag";
import { ApiUtils } from "@/lib/api/utils";
import { ApiTypeUtils } from "@/lib/api/utils/types";
import { Flex, Image, SegmentedControl, Tooltip } from "@mantine/core";
import { ReactNode, useMemo } from "react";

type SegmentFlags = Record<UserTagTag, boolean>;
type SegmentFlagToggleFn = Record<UserTagTag, () => void>;
type SegmentFlagsWithAll = UserTagTag | "All";

export default function FilterTagsControl({
  tags,
  flags,
  flagsToggle,
}: {
  tags: ApiTypeUtils.FilteredUserTag[];
  flags: SegmentFlags;
  flagsToggle: SegmentFlagToggleFn;
}) {
  // https://mantine.dev/core/segmented-control/#controlled
  const segments: {
    label: ReactNode;
    value: SegmentFlagsWithAll;
  }[] = useMemo(
    () => [
      { label: "All", value: "All" },
      ...tags.map((tag) => {
        const metadata = ApiUtils.getMetadataByTagEnum(tag.tag);
        return {
          label: (
            <Flex w={"100%"} justify={"center"}>
              <Tooltip
                label={metadata.name}
                color="dark.4"
                position="top"
                withArrow
              >
                <Image src={metadata.icon} alt={metadata.alt} h={20} w={20} />
              </Tooltip>
            </Flex>
          ),
          value: tag.tag,
        };
      }),
    ],
    [tags],
  );

  // can only have one selected at a time, which is why we can use `.find`
  const currentValue: SegmentFlagsWithAll = useMemo(() => {
    const firstEnabledKeyValue = Object.typedEntries(flags).find(
      ([_flagTag, isFlagActive]) => isFlagActive,
    );

    if (!firstEnabledKeyValue) {
      return "All";
    }

    const firstEnabledKey = firstEnabledKeyValue[0] as UserTagTag;

    return firstEnabledKey;
  }, [flags]);

  const onChange = (value: string) => {
    const segmentKey = value as SegmentFlagsWithAll;

    // disable previous
    if (currentValue !== "All") {
      flagsToggle[currentValue]();
    }

    // enable current
    if (segmentKey !== "All") {
      flagsToggle[segmentKey]();
    }
  };

  return (
    <SegmentedControl
      value={currentValue}
      w="100%"
      variant="light"
      data={segments}
      onChange={onChange}
    />
  );
}
