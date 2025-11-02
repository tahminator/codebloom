import Paginator from "@/components/ui/table/Paginator";
import SearchBox from "@/components/ui/table/SearchBox";
import { useAllLeaderboardsMetadataQuery } from "@/lib/api/queries/leaderboard";
import { timeDiff } from "@/lib/timeDiff";
import { Box, Flex, Overlay, Table, Text, Tooltip } from "@mantine/core";
import { Link } from "react-router-dom";

import AllLeaderboardAdminSkeleton from "./AllLeaderboardAdminSkeleton";
import NewLeaderboardForm from "./new-leaderboard/NewLeaderBoardFormBody";

export default function AllLeaderboardAdmin() {
  const {
    data,
    status,
    goTo,
    page,
    goBack,
    goForward,
    setSearchQuery,
    searchQuery,
    isPlaceholderData,
  } = useAllLeaderboardsMetadataQuery({ pageSize: 5 });

  if (status === "pending") {
    return <AllLeaderboardAdminSkeleton />;
  }

  if (status === "error") {
    return <div>Error</div>;
  }

  if (!data.success) {
    return <div>{data.message}</div>;
  }

  const pageData = data.payload;
  const currentLeaderboard = pageData.items[0];

  return (
    <>
      <Box w="100%" px="lg">
        {" "}
        <Flex>
          <SearchBox
            query={searchQuery}
            onChange={(event) => setSearchQuery(event.currentTarget.value)}
            placeholder="Search for leaderboard"
            mt={"lg"}
            pt={10}
            pr={10}
            style={{
              width: "85%",
            }}
          />
          <NewLeaderboardForm
            currentLeaderboardName={currentLeaderboard.name}
          />
        </Flex>
      </Box>
      <Box style={{ overflowX: "auto" }} m={"lg"} mt={0} pt={0}>
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
              <Table.Th ta={"center"}>Name</Table.Th>
              <Table.Th ta={"center"}>Created</Table.Th>
              <Table.Th ta={"center"}>Ended</Table.Th>
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
            {pageData.items.map((leaderboard, index) => {
              const isCurrentLeaderboard = leaderboard.deletedAt === null;

              return (
                <Table.Tr key={index}>
                  <Table.Td>
                    <Flex
                      direction={"column"}
                      component={Link}
                      to={
                        isCurrentLeaderboard ? "/leaderboard" : (
                          `/leaderboard/${leaderboard.id}`
                        )
                      }
                      className="group"
                    >
                      <Text
                        ta="center"
                        className="transition-all group-hover:text-blue-500 w-max"
                      >
                        {leaderboard.name}
                      </Text>
                    </Flex>
                  </Table.Td>
                  <Table.Td>
                    <Tooltip
                      events={{ hover: true, focus: true, touch: true }}
                      label={new Date(leaderboard.createdAt).toLocaleString()}
                    >
                      <span>{timeDiff(new Date(leaderboard.createdAt))}</span>
                    </Tooltip>
                  </Table.Td>
                  <Table.Td>
                    {!isCurrentLeaderboard && leaderboard.deletedAt ?
                      <Tooltip
                        label={new Date(leaderboard.deletedAt).toLocaleString()}
                        events={{ hover: true, focus: true, touch: true }}
                      >
                        <span>{timeDiff(new Date(leaderboard.deletedAt))}</span>
                      </Tooltip>
                    : "Currently running"}
                  </Table.Td>
                </Table.Tr>
              );
            })}
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
    </>
  );
}
