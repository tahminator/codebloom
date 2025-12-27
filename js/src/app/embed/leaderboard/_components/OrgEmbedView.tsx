import LeaderboardSkeleton from "@/app/leaderboard/_components/LeaderboardSkeleton";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import CustomPagination from "@/components/ui/table/CustomPagination";
import SearchBox from "@/components/ui/table/SearchBox";
import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardUsersQuery } from "@/lib/api/queries/leaderboard";
import { ApiTypeUtils } from "@/lib/api/utils/types";
import getOrdinal from "@/lib/helper/ordinal";
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
import { useEffect, useMemo } from "react";
import { FaArrowLeft, FaArrowRight, FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

import OrgHeader from "./OrgHeader";

export default function OrgLeaderboardEmbed() {
  const {
      status,
      isPlaceholderData,
      data,
      page,
      goTo,
      goBack,
      goForward,
      setSearchQuery,
      searchQuery,
      debouncedQuery,
      filters,
      onFilterReset
    } = useCurrentLeaderboardUsersQuery();

    const activeFilter = useMemo<ApiTypeUtils.FilteredTag | undefined>(() => {
      const active = Object.typedEntries(filters).filter(
        ([, enabled]) => enabled
      );

      return active.length === 1 ? active[0][0] : undefined;
    }, [filters]);


    useEffect(() => {
      const activeCount = Object.typedEntries(filters).filter(
        ([, enabled]) => enabled
      ).length;

      if (activeCount > 1) {
        onFilterReset();
      }
    }, [filters, onFilterReset]);

    if (status === "pending") {
      return <LeaderboardSkeleton />;
    }
  
    if (status === "error") {
      return <Toast message="Sorry, something went wrong." />;
    }
  
    if (!data.success) {
      return <p>Sorry, there are no users to display.</p>;
    }
  
    const pageData = data.payload;
    const [first, second, third] = pageData.items;

    return (
      <>
        <OrgHeader orgTag={activeFilter}/>
        <Center mb="md">
          <Button
            component="a"
            href="https://codebloom.patinanetwork.org"
            target="_blank"
            rel="noopener noreferrer"
          >
            Visit CodeBloom
          </Button>
        </Center>
        <Flex
          direction={{ base: "column", xs: "row" }}
          align={{ base: "center", xs: "flex-end" }}
          justify="center"
          gap="md"
          mb="xl"
        >
          {page === 1 && second && !debouncedQuery && (
            <LeaderboardCard
              placeString={getOrdinal(second.index)}
              sizeOrder={2}
              discordName={second.discordName}
              leetcodeUsername={second.leetcodeUsername}
              totalScore={second.totalScore}
              nickname={second.nickname}
              width={"300px"}
              userId={second.id as string}
              isLoading={isPlaceholderData}
            />
          )}
          {page === 1 && first && !debouncedQuery && (
            <LeaderboardCard
              placeString={getOrdinal(first.index)}
              sizeOrder={1}
              discordName={first.discordName}
              leetcodeUsername={first.leetcodeUsername}
              totalScore={first.totalScore}
              nickname={first.nickname}
              width={"300px"}
              userId={first.id as string}
              isLoading={isPlaceholderData}
            />
          )}
          {page === 1 && third && !debouncedQuery && (
            <LeaderboardCard
              placeString={getOrdinal(third.index)}
              sizeOrder={3}
              discordName={third.discordName}
              leetcodeUsername={third.leetcodeUsername}
              totalScore={third.totalScore}
              nickname={third.nickname}
              width={"300px"}
              userId={third.id as string}
              isLoading={isPlaceholderData}
            />
          )}
        </Flex>
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
              {pageData.items.map(({ index: rank, ...entry }, index) => {
                if (page === 1 && !debouncedQuery && [0, 1, 2].includes(index))
                  return null;
                return (
                  <Table.Tr key={index}>
                    <Table.Td>{rank}</Table.Td>
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
      </>
    );
  }