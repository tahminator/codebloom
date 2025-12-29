import LeaderboardSkeleton from "@/app/leaderboard/_components/LeaderboardSkeleton";
import FilterDropdown from "@/components/ui/dropdown/FilterDropdown";
import FilterDropdownItem from "@/components/ui/dropdown/FilterDropdownItem";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import CustomPagination from "@/components/ui/table/CustomPagination";
import SearchBox from "@/components/ui/table/SearchBox";
import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import { useLeaderboardUsersByIdQuery } from "@/lib/api/queries/leaderboard";
import { ApiUtils } from "@/lib/api/utils";
import { schoolFF, tagFF } from "@/lib/ff";
import getOrdinal from "@/lib/helper/ordinal";
import { theme } from "@/lib/theme";
import {
  Box,
  Table,
  Overlay,
  Flex,
  Tooltip,
  Center,
  Button,
  Image,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord, FaArrowLeft, FaArrowRight } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardWithId({
  leaderboardId,
}: {
  leaderboardId: string;
}) {
  const {
    data,
    status,
    goTo,
    page,
    goBack,
    goForward,
    setSearchQuery,
    searchQuery,
    debouncedQuery,
    filters,
    toggleFilter,
    globalIndex,
    toggleGlobalIndex,
    isPlaceholderData,
  } = useLeaderboardUsersByIdQuery({ leaderboardId });

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
            userId={second.id}
            tags={second.tags}
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
            userId={first.id}
            tags={first.tags}
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
            userId={third.id}
            tags={third.tags}
            isLoading={isPlaceholderData}
          />
        )}
      </Flex>
      <FilterDropdown
        style={{
          marginLeft: "auto",
          display: "block",
        }}
        buttonName="Filters"
      >
        {schoolFF &&
          ApiUtils.getAllSupportedTagEnums().map((tagEnum) => (
            <FilterDropdownItem
              key={tagEnum}
              name={() => {
                const metadata = ApiUtils.getMetadataByTagEnum(tagEnum);

                return (
                  <Flex gap={"xs"} align={"center"}>
                    {metadata.shortName}
                    <Image
                      src={metadata.icon}
                      alt={metadata.alt}
                      style={{ height: "2em", width: "auto" }}
                    />
                  </Flex>
                );
              }}
              value={filters[tagEnum]}
              toggle={() => toggleFilter(tagEnum)}
            />
          ))}
        <FilterDropdownItem
          value={globalIndex}
          toggle={toggleGlobalIndex}
          disabled={!Object.values(filters).some(Boolean)}
          switchMode
          name={
            <Box
              style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}
            >
              Toggle Global Rank
            </Box>
          }
        />
      </FilterDropdown>
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
                    <Tooltip
                      label={
                        "Ability to view profiles at a specific time are not supported yet."
                      }
                      color={"dark.4"}
                    >
                      <Flex
                        direction={"column"}
                        component={Link}
                        to={`/user/${entry.id}`}
                        className="group"
                      >
                        {entry.nickname && (
                          <Flex align="center" gap="xs">
                            <Tooltip
                              label={
                                "This user is a verified member of the Patina Discord server."
                              }
                              color={"dark.4"}
                              withArrow
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
                          </Flex>
                        )}
                        <Flex align="center" gap="xs">
                          <span className="transition-all group-hover:text-blue-500 w-max">
                            <FaDiscord style={{ display: "inline" }} />{" "}
                            {entry.discordName}
                          </span>
                          {tagFF && (
                            <TagList
                              tags={entry.tags || []}
                              size={16}
                              gap="xs"
                            />
                          )}
                        </Flex>
                        <span className="transition-all group-hover:text-blue-500 w-max">
                          <SiLeetcode style={{ display: "inline" }} />{" "}
                          {entry.leetcodeUsername}
                        </span>
                      </Flex>
                    </Tooltip>
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
