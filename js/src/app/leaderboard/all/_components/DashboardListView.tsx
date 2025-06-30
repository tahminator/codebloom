import CustomPagination from "@/components/ui/table/CustomPagination";
import SearchBox from "@/components/ui/table/SearchBox";
import { useAllLeaderboardsMetadataQuery } from "@/lib/api/queries/leaderboard";
import { timeDiff } from "@/lib/timeDiff";
import {
  Box,
  Button,
  Card,
  Center,
  Flex,
  Overlay,
  Table,
  Title,
} from "@mantine/core";
import { FaArrowLeft, FaArrowRight } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function DashboardListView() {
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
  } = useAllLeaderboardsMetadataQuery({});

  if (status === "pending") {
    return <>Pending</>;
  }

  if (status === "error") {
    return <>error</>;
  }

  if (!data.success) {
    return <>{data.message}</>;
  }

  const pageData = data.payload;

  return (
    <Box>
      <Card withBorder padding={"md"} radius={"md"}>
        <Center>
          <Title order={3}>All Leaderboards</Title>
        </Center>
        <SearchBox
          query={searchQuery}
          onChange={(event) => {
            setSearchQuery(event.currentTarget.value);
          }}
          placeholder={"Search by leaderboard name"}
        />
        <Box style={{ overflowX: "auto" }} maw={"100%"} miw={"66%"}>
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
                <Table.Th>Name</Table.Th>
                <Table.Th>Created At</Table.Th>
                <Table.Th>Ended At</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
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
                        <span className="transition-all group-hover:text-blue-500 w-max">
                          <SiLeetcode style={{ display: "inline" }} />
                          {leaderboard.name}
                        </span>
                      </Flex>
                    </Table.Td>
                    <Table.Td>
                      {timeDiff(new Date(leaderboard.createdAt))}
                    </Table.Td>
                    <Table.Td>
                      {!isCurrentLeaderboard ?
                        timeDiff(new Date(leaderboard.deletedAt!))
                      : "Currently running"}
                    </Table.Td>
                  </Table.Tr>
                );
              })}
            </Table.Tbody>
          </Table>
        </Box>
        <Center my={"sm"}>
          <Flex direction={"row"} gap={"sm"}>
            <Button disabled={page === 1} onClick={goBack} size={"compact-sm"}>
              <FaArrowLeft />
            </Button>
            <CustomPagination
              goTo={goTo}
              pages={pageData.pages}
              currentPage={page}
            />
            <Button
              disabled={!pageData.hasNextPage || page >= pageData.pages}
              onClick={() => {
                if (pageData.hasNextPage || page >= pageData.pages) {
                  goForward();
                }
              }}
              size={"compact-sm"}
            >
              <FaArrowRight />
            </Button>
          </Flex>
        </Center>
      </Card>
    </Box>
  );
}
