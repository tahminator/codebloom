import { Flex, ActionIcon } from "@mantine/core";
import { IconChevronLeft, IconChevronRight } from "@tabler/icons-react";
import { Children, ReactNode, ReactElement, useState } from "react";

interface AchievementCarouselProps {
  children: ReactNode;
  visibleCount?: number;
  gap?: string | number;
}

const NAVIGATION_BUTTON_STYLES = {
  enabled: { cursor: "pointer", opacity: 1 } as const,
  disabled: { cursor: "not-allowed", opacity: 0.3 } as const,
};

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
      style={
        disabled ?
          NAVIGATION_BUTTON_STYLES.disabled
        : NAVIGATION_BUTTON_STYLES.enabled
      }
    >
      {icon}
    </ActionIcon>
  );
}

export default function AchievementCarousel({
  children,
  visibleCount = 3,
  gap = "xs",
}: AchievementCarouselProps) {
  const [currentPage, setCurrentPage] = useState(0);

  const itemsArray = Children.toArray(children) as ReactElement[];

  const startIdx = currentPage * visibleCount;
  const visibleItems = itemsArray.slice(startIdx, startIdx + visibleCount);

  const canGoNext =
    currentPage < Math.ceil(itemsArray.length / visibleCount) - 1;
  const canGoPrev = currentPage > 0;

  const handleNext = () => canGoNext && setCurrentPage((prev) => prev + 1);
  const handlePrev = () => canGoPrev && setCurrentPage((prev) => prev - 1);

  if (!itemsArray.length) {
    return null;
  }

  if (itemsArray.length <= visibleCount) {
    return (
      <Flex justify="center" align="center">
        {itemsArray}
      </Flex>
    );
  }

  return (
    <Flex gap="md" align="center" justify="center">
      <Flex gap={gap} wrap="nowrap" align="center">
        {visibleItems}
      </Flex>
      <Flex direction="column" gap={6} align="center">
        <NavigationButton
          onClick={handleNext}
          disabled={!canGoNext}
          icon={<IconChevronRight size={18} />}
        />
        <NavigationButton
          onClick={handlePrev}
          disabled={!canGoPrev}
          icon={<IconChevronLeft size={18} />}
        />
      </Flex>
    </Flex>
  );
}
