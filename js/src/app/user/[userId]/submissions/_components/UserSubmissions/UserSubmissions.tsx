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
} from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";
import { Link } from "react-router-dom";

import TopicFilterPopover from "../TopicFilters/TopicFilterPopover";

export default function UserSubmissions({ userId }: { userId?: string }) {
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
  } = useUserSubmissionsQuery({
    userId,
    tieToUrl: true,
    pageSize: 15,
  });

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

  if (!data.success) {
    return <>{data.message}</>;
  }
  const pageData = data.payload;

  const submissionCard = (
    submission: (typeof pageData.items)[0],
    index: number,
  ) => {
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
        key={index}
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
                fw={isMobile ? 500 : 600}
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
                    {formatTopicName(topic.topicSlug)}
                  </Badge>
                ))}
              </Group>
              <Text size="sm" fw={500}>
                {submission.pointsAwarded} Pts
              </Text>
            </Group>
          )}
          {!isMobile &&
            !(submission.topics && submission.topics.length === 0) && (
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
  };

  return (
    <Box
      pos="relative"
      px={isMobile ? "xs" : undefined}
      w={isMobile ? undefined : "100%"}
      maw={isMobile ? undefined : 925}
      p={isMobile ? undefined : "xs"}
    >
      {!isMobile && (
        <Box ml="auto" display="block">
          <FilterDropdown buttonName="Filters">
          <TopicFilterPopover
            value={topics}
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
          </FilterDropdown>
        </Box>
      )}
      <Group
        justify="space-between"
        align="flex-end"
        mb="sm"
        gap="xs"
        pt={isMobile ? undefined : 10}
      >
        <Box flex={1} miw={0}>
          <SearchBox
            pt={isMobile ? undefined : 10}
            query={searchQuery}
            onChange={(event) => setSearchQuery(event.currentTarget.value)}
            placeholder="Search for submission title"
            w={isMobile ? "100%" : undefined}
          />
        </Box>
        {isMobile && (
          <FilterDropdown buttonName="Filters">
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
          </FilterDropdown>
        )}
      </Group>
      <Box pos="relative">
        {isPlaceholderData && (
          <Overlay zIndex={1000} backgroundOpacity={0.35} blur={3} />
        )}
        <Stack gap="sm" my="sm" align={isMobile ? undefined : "center"}>
          {pageData.items.length === 0 && (
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
          )}
          {pageData.items.map(submissionCard)}
        </Stack>
      </Box>
      <Paginator
        pages={pageData.pages}
        currentPage={page}
        hasNextPage={pageData.hasNextPage}
        goBack={goBack}
        goForward={goForward}
        goTo={goTo}
      />
    </Box>
  );
}
