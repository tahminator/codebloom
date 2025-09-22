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
import { Badge, Box, Overlay, Table, Text, Stack, Group, Card } from "@mantine/core";
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

  if(isMobile) {
    return (
        <Box pos="relative" px="xs">
            {isPlaceholderData && (
                <Overlay zIndex={1000} backgroundOpacity={0.35} blur={4} />
        )}
        <Group justify="space-between" align="flex-end" mb="sm" gap="xs">
          <Box style={{ flex: 1, minWidth: 0}}>
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
                <Box
                  style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}
                >
                  Points Received
                </Box>
              }
            ></FilterDropdownItem>
          </FilterDropdown>
        </Group>
        <Stack gap="sm" my="sm">
          {pageData.items.length === 0 && (
            <>
              <Card withBorder p="md" radius="md" style={{ minHeight: "80px", width: "100%", flex: 1}}>
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
              <Card key={index} withBorder p="sm" radius="md">
                <Stack gap="xs">
                  <Group justify="space-between" align="flex-start">
                    <Group gap="xs" style={{ flex: 1, minWidth: 0 }}>
                      <LanguageIcon size={20} width={20} height={20} />
                      <Text
                        component={Link}
                        to={`/submission/${submission.id}`}
                        className="transition-all hover:text-blue-500"
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
                    <Text size="sm" fw={500}>
                      {submission.pointsAwarded} pts
                    </Text>
                  </Group>
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
    )
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
          pt="10px"
          query={searchQuery}
          onChange={(event) => {
            setSearchQuery(event.currentTarget.value);
          }}
          placeholder={"Search for submission title"}
        />
        <Box style={{ overflowX: "auto" }}>
          <Table
            verticalSpacing={"lg"}
            horizontalSpacing={"xs"}
            withRowBorders={false}
            striped
            my={"sm"}
            pos={"relative"}
          >
            {isPlaceholderData && <Overlay backgroundOpacity={0.35} blur={4} />}
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Lang</Table.Th>
                <Table.Th>Title</Table.Th>
                <Table.Th>Difficulty</Table.Th>
                <Table.Th>Accepted</Table.Th>
                <Table.Th>Pts</Table.Th>
                <Table.Th>Solved</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {pageData.items.length == 0 && (
                <Table.Tr>
                  <Table.Td colSpan={100}>
                    <Text fw={500} ta="center">
                      Nothing found
                    </Text>
                  </Table.Td>
                </Table.Tr>
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
                  <Table.Tr key={index}>
                    <Table.Td>
                      <LanguageIcon size={24} width={24} height={24} />
                    </Table.Td>
                    <Table.Td>
                      <Text
                        component={Link}
                        to={`/submission/${submission.id}`}
                        className="transition-all hover:text-blue-500"
                      >
                        {submission.questionTitle}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Badge ta="center" color={badgeDifficultyColor}>
                        {submission.questionDifficulty}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Badge ta={"center"} color={badgeAcceptedColor}>
                        {Math.round(submission.acceptanceRate * 100)}%
                      </Badge>
                    </Table.Td>
                    <Table.Td>{submission.pointsAwarded}</Table.Td>
                    <Table.Td>
                      {timeDiff(new Date(submission.submittedAt))}
                    </Table.Td>
                  </Table.Tr>
                );
              })}
              {pageData.items.length < pageData.pageSize &&
                Array(pageData.pageSize - pageData.items.length)
                  .fill(0)
                  .map((_, idx) => (
                    <Table.Tr key={idx} opacity={0}>
                      <Table.Td></Table.Td>
                      <Table.Td>
                        <Text></Text>
                      </Table.Td>
                      <Table.Td>
                        <Badge ta="center"></Badge>
                      </Table.Td>
                      <Table.Td>
                        <Badge ta={"center"}></Badge>
                      </Table.Td>
                      <Table.Td></Table.Td>
                      <Table.Td></Table.Td>
                    </Table.Tr>
                  ))}
            </Table.Tbody>
          </Table>
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
    </>
  );
}
