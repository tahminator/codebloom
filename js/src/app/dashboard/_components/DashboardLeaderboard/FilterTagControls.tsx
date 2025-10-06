import { UserTagTag } from "@/lib/api/types/autogen/schema";
import { ApiUtils } from "@/lib/api/utils";
import { ApiTypeUtils } from "@/lib/api/utils/types";
import {
  TagEnumToBooleanFilterObject,
  ToggleTagEnumFn,
} from "@/lib/hooks/useFilters";
import { Flex, Image, SegmentedControl, Tooltip } from "@mantine/core";
import { ReactNode, useMemo } from "react";

type SegmentFlagsWithAll = ApiTypeUtils.FilteredUserTagTag | "All";

export default function FilterTagsControl({
  tags,
  filters,
  toggleFilter,
  onFilterSelected,
}: {
  tags: ApiTypeUtils.FilteredUserTag[];
  filters: TagEnumToBooleanFilterObject;
  toggleFilter: ToggleTagEnumFn;
  onFilterSelected: (value: string | undefined) => void;
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
    const firstEnabledKeyValue = Object.typedEntries(filters).find(
      ([_flagTag, isFlagActive]) => isFlagActive,
    );

    if (!firstEnabledKeyValue) {
      return "All";
    }

    const firstEnabledKey =
      firstEnabledKeyValue[0] as ApiTypeUtils.FilteredUserTagTag;

    return firstEnabledKey;
  }, [filters]);

  const onChange = (value: string) => {
    const segmentKey = value as SegmentFlagsWithAll;

    // school filter after clicking view all
    if (value == "All") {
      onFilterSelected(undefined);
    } else {
      onFilterSelected(
        ApiUtils.getMetadataByTagEnum(segmentKey as UserTagTag).apiKey,
      );
    }

    // disable previous
    if (currentValue !== "All") {
      toggleFilter(currentValue);
    }

    // enable current
    if (segmentKey !== "All") {
      toggleFilter(segmentKey);
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
