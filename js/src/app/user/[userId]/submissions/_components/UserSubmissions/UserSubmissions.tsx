import DateRangePopover from "@/app/user/[userId]/submissions/_components/DateRangePopover/DateRangePopover";
import TopicFilterPopover from "@/app/user/[userId]/submissions/_components/TopicFilters/TopicFilterPopover";
import UserSubmissionsSkeleton from "@/app/user/[userId]/submissions/_components/UserSubmissions/UserSubmissionsSkeleton";
import FilterDropdown from "@/components/ui/dropdown/FilterDropdown";
import FilterDropdownItem from "@/components/ui/dropdown/FilterDropdownItem";
import {
  langNameKey,
  langNameToIcon,
} from "@/components/ui/langname-to-icon/LangNameToIcon";
import Paginator from "@/components/ui/table/Paginator";
import SearchBox from "@/components/ui/table/SearchBox";
import Toast from "@/components/ui/toast/Toast";
import { useUserSubmissionsQuery } from "@/lib/api/queries/user";
import { ApiUtils } from "@/lib/api/utils";
import { timeDiff } from "@/lib/timeDiff";
import {
  Badge,
  Box,
  Overlay,
  Text,
  Stack,
  Group,
  Card,
  Flex,
  Tooltip,
} from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";
import { IconClock } from "@tabler/icons-react";
import d from "dayjs";
import { useMemo } from "react";
import { Link } from "react-router-dom";

const clockIconStyle = {
  position: "absolute" as const,
  top: -8,
  right: -8,
  width: 22,
  height: 22,
  borderRadius: "50%",
  backgroundColor: "var(--mantine-color-teal-7)",
  border: "2px solid var(--mantine-color-dark-7)",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
};

export default function UserSubmissions({ userId }: { userId: string }) {
  const {
    data,
    status,
    page,
    goBack,
    goForward,
    isPlaceholderData,
    goTo,
    searchQuery,
    setSearchQuery,
    pointFilter,
    togglePointFilter,
    topics,
    setTopics,
    clearTopics,
    startDate,
    endDate,
    setStartDate,
    setEndDate,
  } = useUserSubmissionsQuery({
    userId,
    tieToUrl: true,
    pageSize: 15,
  });

  const selectedTopicsSet = useMemo(() => new Set(topics), [topics]);

  const isMobile = useMediaQuery("(max-width: 768px)");

  if (status === "pending") {
    return (
      <>
        <UserSubmissionsSkeleton />
      </>
    );
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong when trying to fetch user's submissions. Please try again later." />
    );
  }

  const pageData = data.payload;

  const dateRangeLabel =
    (startDate || endDate) &&
    `Viewing submissions from ${startDate ? d(startDate).format("MMM D, YYYY") : "now"} to ${endDate ? d(endDate).format("MMM D, YYYY") : "now"}`;

  return (
    <Box
      mt={10}
      pos="relative"
      px={isMobile ? "xs" : undefined}
      w={isMobile ? undefined : "100%"}
      maw={isMobile ? undefined : 925}
      p={isMobile ? undefined : "xs"}
    >
      {!isMobile && (
        <Box display="block" style={{ textAlign: "right" }}>
          <Box pos="relative" display="inline-block">
            <FilterDropdown buttonName="Filters">
              <TopicFilterPopover
                value={topics}
                selectedTopicsSet={selectedTopicsSet}
                onChange={setTopics}
                onClear={clearTopics}
              />
              <FilterDropdownItem
                value={pointFilter}
                toggle={togglePointFilter}
                switchMode
                name={
                  <Flex gap="0.5rem" align="center">
                    Points Received
                  </Flex>
                }
              />
              <DateRangePopover
                startDate={startDate}
                endDate={endDate}
                onStartDateChange={setStartDate}
                onEndDateChange={setEndDate}
              />
            </FilterDropdown>
            {dateRangeLabel && (
              <Tooltip
                label={dateRangeLabel}
                withArrow
                position="bottom"
                withinPortal
              >
                <Box style={clockIconStyle} data-testid="date-range-clock-icon">
                  <IconClock size={13} color="white" stroke={2.5} />
                </Box>
              </Tooltip>
            )}
          </Box>
        </Box>
      )}
      <Group
        justify="space-between"
        align="flex-end"
        mb="sm"
        gap="sm"
        pt={isMobile ? undefined : 10}
      >
        <Box flex={1} miw={0}>
          <SearchBox
            pt={isMobile ? undefined : 0}
            query={searchQuery}
            onChange={(event) => setSearchQuery(event.currentTarget.value)}
            placeholder="Search for submission title"
            w={isMobile ? "100%" : undefined}
          />
        </Box>
        {isMobile && (
          <Box pos="relative" style={{ flexShrink: 0 }}>
            <FilterDropdown buttonName="Filters">
              <TopicFilterPopover
                value={topics}
                selectedTopicsSet={selectedTopicsSet}
                onChange={setTopics}
                onClear={clearTopics}
              />
              <FilterDropdownItem
                value={pointFilter}
                toggle={togglePointFilter}
                switchMode
                name={
                  <Flex gap="0.5rem" align="center">
                    Points Received
                  </Flex>
                }
              />
              <DateRangePopover
                startDate={startDate}
                endDate={endDate}
                onStartDateChange={setStartDate}
                onEndDateChange={setEndDate}
              />
            </FilterDropdown>
            {dateRangeLabel && (
              <Box style={{ ...clockIconStyle, pointerEvents: "none" }}>
                <IconClock size={13} color="white" stroke={2.5} />
              </Box>
            )}
          </Box>
        )}
      </Group>
      <Box pos="relative">
        {isPlaceholderData && (
          <Overlay zIndex={1000} backgroundOpacity={0.35} blur={3} />
        )}
        <Stack gap="sm" my="sm" align={isMobile ? undefined : "center"}>
          {!pageData || pageData.items.length === 0 ?
            <Card
              withBorder
              p="md"
              radius="md"
              mih={80}
              w="100%"
              flex={isMobile ? 1 : undefined}
            >
              <Stack gap="xs" justify="center" align="center" h="100%">
                <Text fw={500} ta="center" c="dimmed">
                  Nothing found.
                </Text>
                <Text size="sm" ta="center" c="dimmed">
                  No submissions has been entered yet.
                </Text>
              </Stack>
            </Card>
          : pageData.items.map((submission) => {
              const badgeDifficultyColor = (() => {
                if (submission.questionDifficulty === "Easy") {
                  return undefined;
                }
                if (submission.questionDifficulty === "Medium") {
                  return "yellow";
                }
                if (submission.questionDifficulty === "Hard") {
                  return "red";
                }
                return undefined;
              })();
              const badgeAcceptedColor = (() => {
                const acceptanceRate = submission.acceptanceRate * 100;
                if (acceptanceRate >= 75) {
                  return undefined;
                }
                if (acceptanceRate >= 50) {
                  return "yellow";
                }
                if (acceptanceRate >= 0) {
                  return "red";
                }
                return undefined;
              })();
              const LanguageIcon =
                langNameToIcon[submission.language as langNameKey] ||
                langNameToIcon["default"];
              return (
                <Card
                  key={submission.id}
                  withBorder
                  p={isMobile ? "sm" : "md"}
                  radius="md"
                  w="100%"
                  component={Link}
                  to={`/submission/${submission.id}`}
                  className="transition-all hover:brightness-110"
                >
                  <Stack gap="xs">
                    <Group justify="space-between" align="flex-start">
                      <Group gap="xs" flex={1} miw={0}>
                        <LanguageIcon
                          size={isMobile ? 20 : 22}
                          width={isMobile ? 20 : 22}
                          height={isMobile ? 20 : 22}
                        />
                        <Text
                          size={isMobile ? "sm" : undefined}
                          fw={500}
                          lh={1.3}
                          flex={1}
                        >
                          {submission.questionTitle}
                        </Text>
                      </Group>
                      <Text size="xs" c="dimmed">
                        {timeDiff(new Date(submission.submittedAt))}
                      </Text>
                    </Group>
                    <Group gap="xs" wrap="wrap">
                      <Badge size="sm" color={badgeDifficultyColor}>
                        {submission.questionDifficulty}
                      </Badge>
                      <Badge size="sm" color={badgeAcceptedColor}>
                        {Math.round(submission.acceptanceRate * 100)}%
                      </Badge>
                    </Group>
                    {submission.topics && submission.topics.length > 0 && (
                      <Group justify="space-between">
                        <Group gap="xs" wrap="wrap">
                          {submission.topics.map((topic) => (
                            <Badge
                              key={topic.id}
                              size="xs"
                              variant={isMobile ? "light" : "filled"}
                              color={isMobile ? "gray" : "gray.4"}
                            >
                              {
                                ApiUtils.getTopicEnumMetadataByTopicEnum(
                                  topic.topic,
                                ).name
                              }
                            </Badge>
                          ))}
                        </Group>
                        <Text size="sm" fw={500}>
                          {submission.pointsAwarded} Pts
                        </Text>
                      </Group>
                    )}
                    {!isMobile &&
                      !(submission.topics && submission.topics.length > 0) && (
                        <Group justify="space-between">
                          <Text size="xs" c="dimmed">
                            -
                          </Text>
                          <Text size="sm" fw={500}>
                            {submission.pointsAwarded} Pts
                          </Text>
                        </Group>
                      )}
                  </Stack>
                </Card>
              );
            })
          }
        </Stack>
      </Box>
      <Paginator
        pages={pageData?.pages ?? 0}
        currentPage={page}
        hasNextPage={pageData?.hasNextPage ?? false}
        goBack={goBack}
        goForward={goForward}
        goTo={goTo}
      />
    </Box>
  );
}
