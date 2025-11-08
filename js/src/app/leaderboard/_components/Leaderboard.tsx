import LeaderboardSkeleton from "@/app/leaderboard/_components/LeaderboardSkeleton";
import FilterDropdown from "@/components/ui/dropdown/FilterDropdown";
import FilterDropdownItem from "@/components/ui/dropdown/FilterDropdownItem";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import CustomPagination from "@/components/ui/table/CustomPagination";
import SearchBox from "@/components/ui/table/SearchBox";
import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardUsersQuery } from "@/lib/api/queries/leaderboard";
import { ApiUtils } from "@/lib/api/utils";
import { schoolFF, tagFF } from "@/lib/ff";
import getOrdinal from "@/lib/helper/ordinal";
import { theme } from "@/lib/theme";
import {
  Box,
  Button,
  Card,
  Center,
  Flex,
  Image,
  Overlay,
  Stack,
  Text,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaArrowLeft, FaArrowRight, FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardIndex() {
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
    isAnyFilterEnabled,
    globalIndex,
    toggleGlobalIndex,
    onFilterReset,
    isPlaceholderData,
  } = useCurrentLeaderboardUsersQuery();

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
  const cardItems = pageData.items.filter((_, index) => {
    if (page === 1 && !debouncedQuery && [0, 1, 2].includes(index)) {
      return false;
    }
    return true;
  });

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
        className="ml-auto block"
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
                      className="h-[2em] w-auto"
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
          disabled={!isAnyFilterEnabled}
          switchMode
          name={
            <Box
              className="flex gap-2 items-center"
            >
              Toggle Global Rank
            </Box>
          }
        />
        <Button
          variant="subtle"
          color="red"
          onClick={() => onFilterReset()}
          fullWidth
          disabled={!isAnyFilterEnabled && !globalIndex}
        >
          Clear Filters
        </Button>
      </FilterDropdown>
      <SearchBox
        query={searchQuery}
        onChange={(event) => {
          setSearchQuery(event.currentTarget.value);
        }}
        placeholder={"Search for User"}
      />
      <Box pos="relative" my="lg">
        {isPlaceholderData && (
          <Overlay zIndex={1000} backgroundOpacity={0.35} blur={4} />
        )}
        <Stack gap="md">
          {cardItems.map((entry) => {
            return (
              <Card
                key={entry.id}
                component={Link}
                to={`/user/${entry.id}`}
                shadow="sm"
                padding="lg"
                radius="md"
                withBorder
                bg={theme.colors.dark[7]}
                className="boarder-dark-5"
              >
                <Flex
                  direction="row"
                  justify="space-between"
                  align="center"
                  gap="md"
                >
                  <Flex className="flex flex-1 item-center gap-4">
                    <Text
                      size="xl"
                      fw={700}
                      c={theme.colors.patina[4]}
                      className="min-w-[50px]"
                    >
                      #{entry.index}
                    </Text>
                    <Flex direction="column" gap="xs">
                      {entry.nickname && (
                        <Flex align="center" gap={5}>
                          <Tooltip
                            label="This user is a verified member of the Patina Discord server."
                            color="dark.4"
                          >
                            <Flex align="center" gap={4}>
                              <IconCircleCheckFilled
                                color={theme.colors.patina[4]}
                                size={18}
                              />
                              <Text
                                fw={600}
                                size="md"
                                className="transition-all group-hover:text-blue-500"
                              >
                                {entry.nickname}
                              </Text>
                            </Flex>
                          </Tooltip>
                          {tagFF && entry.tags && entry.tags.length > 0 && (
                            <Box mt={4}>
                              <TagList tags={entry.tags} size={16} gap="xs" />
                            </Box>
                          )}
                        </Flex>
                      )}
                      <Flex
                        direction={{ base: "column", xs: "row" }}
                        gap={{ base: "xs", xs: "md" }}
                        align={{ base: "flex-start", xs: "center" }}
                      >
                        <Flex align="center" gap={6}>
                          <FaDiscord size={16} />
                          <Text
                            size="sm"
                            className="transition-all group-hover:text-blue-500"
                          >
                            {entry.discordName}
                          </Text>
                        </Flex>
                        <Flex align="center" gap={6}>
                          <SiLeetcode size={16} />
                          <Text
                            size="sm"
                            className="transition-all group-hover:text-blue-500"
                          >
                            {entry.leetcodeUsername}
                          </Text>
                        </Flex>
                      </Flex>
                    </Flex>
                  </Flex>
                  <Text
                    size="lg"
                    fw={600}
                    className="transition-all group-hover:text-white min-w-[100px] text-right"
                  >
                    {entry.totalScore} Pts
                  </Text>
                </Flex>
              </Card>
            );
          })}
        </Stack>
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
