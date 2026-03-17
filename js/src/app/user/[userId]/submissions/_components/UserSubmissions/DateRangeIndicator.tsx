import { Box, Tooltip } from "@mantine/core";
import { IconClock } from "@tabler/icons-react";

type DateValue = Date | string | null | undefined;

interface DateRangeIndicatorProps {
  readonly startDate: DateValue;
  readonly endDate: DateValue;
}

function formatDateRange(startDate: DateValue, endDate: DateValue): string {
  const fmt = (d: Date | string) =>
    new Date(d).toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
      year: "numeric",
    });

  if (startDate && endDate)
    return `Filtered: ${fmt(startDate)} – ${fmt(endDate)}`;
  if (startDate) return `Filtered: From ${fmt(startDate)}`;
  if (endDate) return `Filtered: Until ${fmt(endDate)}`;
  return "";
}

export default function DateRangeIndicator({
  startDate,
  endDate,
}: Readonly<DateRangeIndicatorProps>) {
  const isActive = !!startDate || !!endDate;

  if (!isActive) return null;

  const label = formatDateRange(startDate, endDate);

  return (
    <Tooltip
      label={label}
      withArrow
      position="top"
      events={{ hover: true, focus: false, touch: false }}
    >
      <Box
        data-testid="date-range-indicator"
        pos="absolute"
        top={-8}
        right={-8}
        bg="green.8"
        c="white"
        w={22}
        h={22}
        display="flex"
        style={{
          zIndex: 10,
          borderRadius: "50%",
          border: "2px solid white",
          alignItems: "center",
          justifyContent: "center",
          flexShrink: 0,
        }}
      >
        <IconClock size={13} />
      </Box>
    </Tooltip>
  );
}
