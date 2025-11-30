import { usePagination } from "@/lib/hooks/usePagination";
import { Flex, ActionIcon } from "@mantine/core";
import { IconChevronLeft, IconChevronRight } from "@tabler/icons-react";
import { ReactElement } from "react";

interface AchievementCarouselProps {
  items: ReactElement[];
  visibleCount?: number;
  gap?: string | number;
  enableCarousel?: boolean;
}

export default function AchievementCarousel({
  items,
  visibleCount = 3,
  gap = "xs",
  enableCarousel = false,
}: AchievementCarouselProps) {
  const totalPages = Math.ceil(items.length / visibleCount);
  const { page, goBack, goForward } = usePagination({
    tieToUrl: false,
  });

  if (!items.length) return <></>;

  const startIdx = (page - 1) * visibleCount;
  const visibleItems = enableCarousel
    ? items.slice(startIdx, startIdx + visibleCount)
    : items;

  const canGoBack = page > 1;
  const canGoForward = page < totalPages;
  const showArrows = enableCarousel && items.length > visibleCount;

  return (
    <Flex gap="md" align="center" justify="center">
      <Flex gap={gap} wrap="nowrap" align="center">
        {visibleItems}
      </Flex>
      {showArrows && (
        <Flex direction="column" gap="xs" align="center">
          <NavigationButton
            onClick={goForward}
            disabled={!canGoForward}
            icon={<IconChevronRight size={18} />}
          />
          <NavigationButton
            onClick={goBack}
            disabled={!canGoBack}
            icon={<IconChevronLeft size={18} />}
          />
        </Flex>
      )}
    </Flex>
  );
}

interface NavigationButtonProps {
  onClick: () => void;
  disabled: boolean;
  icon: ReactElement;
}

function NavigationButton({ onClick, disabled, icon }: NavigationButtonProps) {
  return (
    <ActionIcon
      variant="subtle"
      color="gray"
      onClick={onClick}
      disabled={disabled}
      size="sm"
    >
      {icon}
    </ActionIcon>
  );
}
