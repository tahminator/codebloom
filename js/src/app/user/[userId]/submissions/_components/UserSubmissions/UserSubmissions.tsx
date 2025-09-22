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
import { Badge, Box, Overlay, Table, Text } from "@mantine/core";
import { Link } from "react-router-dom";

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
  } = useUserSubmissionsQuery({
    userId,
    tieToUrl: true,
  });

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

  return (
    <>
      <Box maw={"100%"} miw={"66%"} p="md">
        <FilterDropdown
          style={{
            marginLeft: "auto",
            display: "block",
          }}
          buttonName="Filters"
        >
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
            {isPlaceholderData && (
              <Overlay zIndex={1000} backgroundOpacity={0.35} blur={4} />
            )}
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
