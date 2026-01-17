import LeaderboardSkeleton from "@/app/leaderboard/_components/LeaderboardSkeleton";
import FilterDropdown from "@/components/ui/dropdown/FilterDropdown";
import FilterDropdownItem from "@/components/ui/dropdown/FilterDropdownItem";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import CustomPagination from "@/components/ui/table/CustomPagination";
import SearchBox from "@/components/ui/table/SearchBox";
import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import {
  useCurrentLeaderboardUsersQuery,
  useLeaderboardUsersByIdQuery,
} from "@/lib/api/queries/leaderboard";
import { ApiUtils } from "@/lib/api/utils";
import { schoolFF, tagFF } from "@/lib/ff";
import getOrdinal from "@/lib/helper/ordinal";
import { useLeaderboardSubmissionsUrl } from "@/lib/hooks/useLeaderboardSubmissionsUrl";
import { theme } from "@/lib/theme";
import {
  Box,
  Button,
  Card,
  Center,
  Divider,
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

export function CurrentLeaderboard() {
  const query = useCurrentLeaderboardUsersQuery();
  return <LeaderboardIndex query={query} />;
}

export function LeaderboardById({ leaderboardId }: { leaderboardId: string }) {
  const query = useLeaderboardUsersByIdQuery({ leaderboardId });
  return <LeaderboardIndex query={query} isPrevious />;
}

function LeaderboardIndex({
  query,
  isPrevious = false,
}: {
  query: ReturnType<typeof useCurrentLeaderboardUsersQuery>;
  isPrevious?: boolean;
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
  } = useCurrentLeaderboardUsersQuery();

  const { getUserSubmissionsUrl, startDate, endDate } =
    useLeaderboardSubmissionsUrl();

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

  const PossibleTooltip = ({ children }: { children: JSX.Element }) =>
    isPrevious ?
      <Tooltip
        label="Ability to view profiles at a specific time are not supported yet."
        color="dark.4"
      >
        {children}
      </Tooltip>
    : <>{children}</>;

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
            startDate={startDate}
            endDate={endDate}
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
            startDate={startDate}
            endDate={endDate}
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
            startDate={startDate}
            endDate={endDate}
          />
        )}
      </Flex>
      <Flex
        justify="space-between"
        align="center"
        mb="md"
        direction={{ base: "column", sm: "row" }}
        gap="md"
      >
        <FilterDropdown
          style={{ marginLeft: "auto", display: "block" }}
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
            disabled={!isAnyFilterEnabled}
            switchMode
            name={
              <Flex gap="xs" align="center">
                Toggle Global Rank
              </Flex>
            }
          />
          <Button
            variant="subtle"
            color="red"
            onClick={onFilterReset}
            fullWidth
            disabled={!isAnyFilterEnabled && !globalIndex}
          >
            Clear Filters
          </Button>
        </FilterDropdown>
      </Flex>
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
          {cardItems.map((entry) => (
            <PossibleTooltip key={entry.id}>
              <Card
                component={Link}
                to={isPrevious ? "#" : getUserSubmissionsUrl(entry.id)}
                shadow="sm"
                padding="lg"
                radius="md"
                withBorder
                bg={theme.colors.dark[7]}
                styles={{
                  root: {
                    borderColor: theme.colors.dark[5],
                  },
                }}
                style={{
                  transition: "all 0.2s ease",
                  textDecoration: "none",
                }}
              >
                <Flex
                  direction="row"
                  justify="space-between"
                  align="center"
                  gap="md"
                >
                  <Flex align="center" gap="md">
                    <Text
                      size="xl"
                      fw={700}
                      c={theme.colors.patina[4]}
                      miw={50}
                    >
                      #{entry.index}
                    </Text>
                    <Flex direction="column" gap="xs">
                      <Stack gap="xs">
                        <Flex
                          direction={{ base: "column", xs: "row" }}
                          gap={{ base: "xs", xs: "md" }}
                          align={{ base: "flex-start", xs: "center" }}
                        >
                          <Flex align="center" gap={6}>
                            <FaDiscord size={16} />
                            <Text size="md" fw={600}>
                              {entry.discordName}
                            </Text>
                          </Flex>
                          <Flex align="center" gap={6}>
                            <SiLeetcode size={16} />
                            <Text size="md" fw={600}>
                              {entry.leetcodeUsername}
                            </Text>
                          </Flex>
                        </Flex>
                        {(entry.nickname ||
                          (tagFF && entry.tags?.length > 0)) && (
                          <Flex align="center" gap={5}>
                            {entry.nickname && (
                              <Tooltip label="This user is a verified member of the Patina Discord server.">
                                <Flex align="center" gap={5}>
                                  <IconCircleCheckFilled
                                    color={theme.colors.patina[4]}
                                    size={18}
                                  />
                                  <Text size="sm">{entry.nickname}</Text>
                                </Flex>
                              </Tooltip>
                            )}
                            {entry.nickname &&
                              tagFF &&
                              entry.tags?.length > 0 && (
                                <Divider orientation="vertical" h={20} />
                              )}
                            {tagFF && entry.tags?.length > 0 && (
                              <TagList tags={entry.tags} size={16} gap="xs" />
                            )}
                          </Flex>
                        )}
                      </Stack>
                    </Flex>
                  </Flex>
                  <Text size="lg" fw={600} miw={100} ta="right">
                    {entry.totalScore} Pts
                  </Text>
                </Flex>
              </Card>
            </PossibleTooltip>
          ))}
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
