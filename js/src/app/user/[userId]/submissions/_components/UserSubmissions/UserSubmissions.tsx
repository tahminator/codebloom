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

  if (isMobile) {
    return (
      <Box pos="relative" px="xs">
        {isPlaceholderData && <Overlay zIndex={1000} opacity={0.35} blur={4} />}
        <Group justify="space-between" align="flex-end" mb="sm" gap="xs">
          <Box style={{ flex: 1, minWidth: 0 }}>
            <SearchBox
              query={searchQuery}
              onChange={(event) => {
                setSearchQuery(event.currentTarget.value);
              }}
              placeholder={"Search for submission title"}
              style={{ width: "100%" }}
            />
          </Box>
          <FilterDropdown buttonName="Filters">
            <FilterDropdownItem
              value={pointFilter}
              toggle={togglePointFilter}
              switchMode
              name={
                <Flex
                  style={{
                    gap: "0.5rem",
                    alignItems: "center",
                  }}
                >
                  Points Received
                </Flex>
              }
            ></FilterDropdownItem>
          </FilterDropdown>
        </Group>
        <Stack gap="sm" my="sm">
          {pageData.items.length === 0 && (
            <>
              <Card
                withBorder
                p="md"
                radius="md"
                style={{ minHeight: "80px", width: "100%", flex: 1 }}
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
            </>
          )}
          {pageData.items.map((submission, index) => {
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
                p="sm"
                radius="md"
                component={Link}
                to={`/submission/${submission.id}`}
                className="transition-all hover:brightness-110"
                style={{
                  cursor: "pointer",
                  textDecoration: "none",
                  color: "inherit",
                }}
              >
                <Stack gap="xs">
                  <Group justify="space-between" align="flex-start">
                    <Group gap="xs" style={{ flex: 1, minWidth: 0 }}>
                      <LanguageIcon size={20} width={20} height={20} />
                      <Text
                        size="sm"
                        fw={500}
                        style={{
                          flex: 1,
                          wordBreak: "break-word",
                          lineHeight: 1.3,
                        }}
                      >
                        {submission.questionTitle}
                      </Text>
                    </Group>
                    <Text size="xs" c="dimmed" style={{ flexShrink: 0 }}>
                      {timeDiff(new Date(submission.submittedAt))}
                    </Text>
                  </Group>
                  <Group justify="space-between" wrap="wrap" gap="xs">
                    <Group gap="xs">
                      <Badge size="sm" color={badgeDifficultyColor}>
                        {submission.questionDifficulty}
                      </Badge>
                      <Badge size="sm" color={badgeAcceptedColor}>
                        {Math.round(submission.acceptanceRate * 100)}%
                      </Badge>
                    </Group>
                  </Group>
                  {submission.topics && submission.topics.length > 0 && (
                    <Group justify="space-between">
                      <Group gap="xs" wrap="wrap">
                        {submission.topics.map((topic) => (
                          <Badge
                            key={topic.id}
                            size="xs"
                            variant="outline"
                            color="gray"
                          >
                            {formatTopicName(topic.topicSlug)}
                          </Badge>
                        ))}
                      </Group>
                      <Text size="sm" fw={500}>
                        {submission.pointsAwarded} pts
                      </Text>
                    </Group>
                  )}
                </Stack>
              </Card>
            );
          })}
        </Stack>
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
  return (
    <>
      <Box maw={"100%"} miw={"66%"} p="xs">
        <FilterDropdown
          style={{
            marginLeft: "auto",
            display: "block",
          }}
          buttonName="Filters"
        >
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
              <Box
                style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}
              >
                Points Received
              </Box>
            }
          ></FilterDropdownItem>
        </FilterDropdown>
        <SearchBox
          style={{ paddingTop: 10 }}
          query={searchQuery}
          onChange={(e) => setSearchQuery(e.currentTarget.value)}
          placeholder={"Search for submission title"}
        />
        <Stack gap="sm" my="sm">
          {pageData.items.length === 0 && (
            <>
              <Card
                withBorder
                p="md"
                radius="md"
                style={{ minHeight: 80, width: "100%", maxWidth: 950 }}
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
            </>
          )}
        </Stack>
        <Box
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: 12,
          }}
          my="md"
        >
          {pageData.items.map((submission, index) => {
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
                p="md"
                radius="md"
                style={{
                  width: "100%",
                  maxWidth: "950px",
                  cursor: "pointer",
                  textDecoration: "none",
                  color: "inherit",
                }}
                component={Link}
                to={`/submission/${submission.id}`}
                className="transition-all hover:brightness-110"
              >
                <Stack gap="xs">
                  <Group justify="space-between" align="flex-start">
                    <Group gap="xs" style={{ flex: 1, minWidth: 0 }}>
                      <LanguageIcon size={22} width={22} height={22} />
                      <Text
                        fw={600}
                        style={{
                          flex: 1,
                          wordBreak: "break-word",
                          lineHeight: 1.35,
                        }}
                      >
                        {submission.questionTitle}
                      </Text>
                    </Group>
                    <Text size="xs" c="dimmed" style={{ flexShrink: 0 }}>
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
                  <Group justify="space-between">
                    <Group gap="xs" wrap="wrap">
                      {submission.topics?.length ?
                        submission.topics.map((topic) => (
                          <Badge
                            key={topic.id}
                            size="xs"
                            variant="outline"
                            color="gray"
                          >
                            {formatTopicName(topic.topicSlug)}
                          </Badge>
                        ))
                      : <Text size="xs" c="dimmed">
                          -
                        </Text>
                      }
                    </Group>
                    <Text size="sm" fw={500}>
                      {submission.pointsAwarded} Pts
                    </Text>
                  </Group>
                </Stack>
              </Card>
            );
          })}
        </Box>
      </Box>
      <Paginator
        pages={pageData.pages}
        currentPage={page}
        hasNextPage={pageData.hasNextPage}
        goBack={goBack}
        goForward={goForward}
        goTo={goTo}
      />
    </>
  );
}
