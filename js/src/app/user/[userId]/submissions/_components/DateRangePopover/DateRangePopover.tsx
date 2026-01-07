import { Button, Flex, Popover } from "@mantine/core";
import { DatePicker, DatePickerPreset } from "@mantine/dates";
import { useDisclosure } from "@mantine/hooks";
import d from "dayjs";
import { useMemo } from "react";

const FMT_STRING = "YYYY-MM-DD";

const datePresets: DatePickerPreset<"range">[] = [
  {
    value: [
      d().subtract(1, "day").startOf("day").format(FMT_STRING),
      d().endOf("day").format(FMT_STRING),
    ],
    label: "Past day",
  },
  {
    value: [
      d().subtract(1, "week").startOf("day").format(FMT_STRING),
      d().endOf("day").format(FMT_STRING),
    ],
    label: "Past week",
  },
  {
    value: [
      d().subtract(1, "month").startOf("day").format(FMT_STRING),
      d().endOf("day").format(FMT_STRING),
    ],
    label: "Past month",
  },
  {
    value: [
      d().subtract(1, "year").startOf("day").format(FMT_STRING),
      d().endOf("day").format(FMT_STRING),
    ],
    label: "Past year",
  },
];

export default function DateRangePopover({
  startDate,
  endDate,
  onStartDateChange,
  onEndDateChange,
}: {
  startDate?: string;
  endDate?: string;
  onStartDateChange: (s: string | undefined) => void;
  onEndDateChange: (s: string | undefined) => void;
}) {
  const dateRange = useMemo(
    () => [
      startDate ? d(startDate).toDate() : null,
      endDate ? d(endDate).toDate() : null,
    ],
    [endDate, startDate],
  ) as [Date | null, Date | null];
  const [opened, { toggle }] = useDisclosure();

  const onClear = () => {
    onStartDateChange(undefined);
    onEndDateChange(undefined);
  };

  return (
    <Popover
      opened={opened}
      position="bottom-start"
      closeOnEscape
      withinPortal={false}
      shadow="md"
      transitionProps={{ keepMounted: false }}
    >
      <Popover.Target>
        <Button fullWidth variant="light" color="gray" onClick={toggle}>
          {(() => {
            if (!startDate && !endDate) {
              return "Choose date range";
            }

            return `From ${startDate ? d(startDate).format("YYYY/MM/DD") : "now"} to ${endDate ? d(endDate).format("YYYY/MM/DD") : "now"}`;
          })()}
        </Button>
      </Popover.Target>
      <Popover.Dropdown w={400}>
        <DatePicker
          aria-label="Start date of submissions"
          type="range"
          value={dateRange}
          onChange={(v) => {
            const [start, end] = v;
            onStartDateChange(
              start ? d(start).startOf("day").format(FMT_STRING) : undefined,
            );
            onEndDateChange(
              end ? d(end).endOf("day").format(FMT_STRING) : undefined,
            );
          }}
          maxDate={d().toDate()}
          presets={datePresets}
        />
        <Flex mt="sm" align="center">
          <Button variant={"filled"} color="red" w={"100%"} onClick={onClear}>
            Reset
          </Button>
        </Flex>
      </Popover.Dropdown>
    </Popover>
  );
}
