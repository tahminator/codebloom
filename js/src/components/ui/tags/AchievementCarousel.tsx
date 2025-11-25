import { Flex, ActionIcon } from "@mantine/core";
import { IconChevronLeft, IconChevronRight } from "@tabler/icons-react";
import { Children, ReactNode, ReactElement, useState } from "react";

interface AchievementCarouselProps {
  children: ReactNode;
  visibleCount?: number;
  gap?: string | number;
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

export default function AchievementCarousel({
  children,
  visibleCount = 3,
  gap = "xs",
}: AchievementCarouselProps) {
  const [currentPage, setCurrentPage] = useState(0);

  const itemsArray = Children.toArray(children) as ReactElement[];

  if (!itemsArray.length) return null;

  const startIdx = currentPage * visibleCount;
  const visibleItems = itemsArray.slice(startIdx, startIdx + visibleCount);

  const totalPages = Math.ceil(itemsArray.length / visibleCount);
  const canGoNext = currentPage < totalPages - 1;
  const canGoPrev = currentPage > 0;

  const handleNext = () => setCurrentPage((prev) => prev + 1);
  const handlePrev = () => setCurrentPage((prev) => prev - 1);

  const showArrows = itemsArray.length >= visibleCount;

  return (
    <Flex gap="md" align="center" justify="center">
      <Flex gap={gap} wrap="nowrap" align="center">
        {visibleItems}
      </Flex>
      {showArrows && (
        <Flex direction="column" gap="xs" align="center">
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
      )}
    </Flex>
  );
}
