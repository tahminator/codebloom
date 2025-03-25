import LeaderboardMetadata from "@/app/leaderboard/_components/LeaderboardMetadata/LeaderboardMetadata";
import LeaderboardSkeleton from "@/app/leaderboard/_components/LeaderboardSkeleton";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import CustomPagination from "@/components/ui/table/CustomPagination";
import SearchBox from "@/components/ui/table/SearchBox";
import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardUsersQuery } from "@/lib/api/queries/leaderboard";
import { theme } from "@/lib/theme";
import {
  Box,
  Button,
  Center,
  Flex,
  Overlay,
  Table,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaArrowLeft, FaArrowRight, FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardIndex() {
  const { data, status, goTo, page, goBack, goForward,setSearchQuery,searchQuery, isPlaceholderData } =
    useCurrentLeaderboardUsersQuery({});

  if (status === "pending") {
    return <LeaderboardSkeleton />;
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.success) {
    return <p>Sorry, there are no users to display.</p>;
  }

  const pageData = data.data;
  const [first, second, third] = pageData.data;

  return (
    <div style={{ padding: "1rem" }}>
      <LeaderboardMetadata />
      <div
        className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
        style={{ marginBottom: "2rem" }}
      >
        {page === 1 && second && !searchQuery&& (
          <LeaderboardCard
            placeString={"Second"}
            discordName={second.discordName}
            leetcodeUsername={second.leetcodeUsername}
            totalScore={second.totalScore}
            nickname={second.nickname}
            width={"300px"}
            userId={second.id}
          />
        )}
        {page === 1 && first && !searchQuery && (
          <LeaderboardCard
            placeString={"First"}
            discordName={first.discordName}
            leetcodeUsername={first.leetcodeUsername}
            totalScore={first.totalScore}
            nickname={first.nickname}
            width={"300px"}
            userId={first.id}
          />
        )}
        {page === 1 && third && !searchQuery && (
          <LeaderboardCard
            placeString={"Third"}
            discordName={third.discordName}
            leetcodeUsername={third.leetcodeUsername}
            totalScore={third.totalScore}
            nickname={third.nickname}
            width={"300px"}
            userId={third.id}
          />
        )}
        </div>
        <SearchBox
            query={searchQuery}
            onChange={(event) => {
                setSearchQuery(event.currentTarget.value);
            }}
            placeholder={"Search for User"}
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
              <Table.Th>#</Table.Th>
              <Table.Th>Name</Table.Th>
              <Table.Th>Pts</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {pageData.data.map((entry, index) => {
              if (page === 1 && !searchQuery && [0, 1, 2].includes(index)) return null;
              return (
                <Table.Tr key={index}>
                  <Table.Td>
                    {index + 1 + (page - 1) * pageData.pageSize}
                  </Table.Td>
                  <Table.Td>
                    <Flex
                      direction={"column"}
                      component={Link}
                      to={`/user/${entry.id}`}
                      className="group"
                    >
                      {entry.nickname ?
                        <Tooltip
                          label={
                            "This user is a verified member of the Patina Discord server."
                          }
                          color={"dark.4"}
                        >
                          <span className="transition-all group-hover:text-blue-500 w-max">
                            <IconCircleCheckFilled
                              className="inline"
                              color={theme.colors.patina[4]}
                              z={5000000}
                              size={20}
                            />{" "}
                            {entry.nickname}
                          </span>
                        </Tooltip>
                      : <span className="transition-all group-hover:text-blue-500 w-max">
                          <FaDiscord style={{ display: "inline" }} />{" "}
                          {entry.discordName}
                        </span>
                      }
                      <span className="transition-all group-hover:text-blue-500 w-max">
                        <SiLeetcode style={{ display: "inline" }} />{" "}
                        {entry.leetcodeUsername}
                      </span>
                    </Flex>
                  </Table.Td>
                  <Table.Td>{entry.totalScore}</Table.Td>
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
    </div>
  );
}
