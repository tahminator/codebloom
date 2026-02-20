import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import { useGetAllUsersInfiniteQuery } from "@/lib/api/queries/user";
import { tagFF } from "@/lib/ff";
import { theme } from "@/lib/theme";
import {
  Box,
  Card,
  Center,
  Divider,
  Flex,
  Loader,
  Skeleton,
  Stack,
  Text,
  TextInput,
  Tooltip,
} from "@mantine/core";
import { useIntersection } from "@mantine/hooks";
import { IconCircleCheckFilled, IconSearch } from "@tabler/icons-react";
import { useEffect } from "react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function UserSearch() {
  const {
    status,
    searchQuery,
    setSearchQuery,
    debouncedQuery,
    allUsers,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useGetAllUsersInfiniteQuery({ tieToUrl: true });

  const { ref, entry } = useIntersection({
    root: null,
    threshold: 0.5,
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  const showResults = debouncedQuery.length > 0;

  return (
    <Stack gap="lg" p="md">
      <Center>
        <TextInput
          value={searchQuery}
          onChange={(event) => setSearchQuery(event.currentTarget.value)}
          placeholder="Search for a user..."
          leftSection={<IconSearch size={18} />}
          size="lg"
          w="100%"
          maw={600}
          radius="md"
          styles={{
            input: {
              backgroundColor: theme.colors.dark[6],
              borderColor: theme.colors.dark[4],
              "&:focus": {
                borderColor: theme.colors.patina[4],
              },
            },
          }}
        />
      </Center>

      {!showResults && (
        <Center py="xl">
          <Text c="dimmed" size="lg">
            Enter a search query to find users
          </Text>
        </Center>
      )}

      {showResults && status === "pending" && <UserSearchSkeleton />}

      {showResults && status === "success" && (
        <Box>
          <Stack gap="md">
            {allUsers.length === 0 && (
              <Center py="xl">
                <Text c="dimmed">
                  No users found matching "{debouncedQuery}"
                </Text>
              </Center>
            )}
            {allUsers.map((user) => (
              <Card
                key={user.id}
                component={Link}
                to={`/user/${user.id}`}
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
                              {user.discordName}
                            </Text>
                          </Flex>
                          {user.leetcodeUsername && (
                            <Flex align="center" gap={6}>
                              <SiLeetcode size={16} />
                              <Text size="md" fw={600}>
                                {user.leetcodeUsername}
                              </Text>
                            </Flex>
                          )}
                        </Flex>
                        {(user.nickname ||
                          (tagFF && user.tags && user.tags.length > 0)) && (
                          <Flex align="center" gap={5}>
                            {user.nickname && (
                              <Tooltip label="This user is a verified member of the Patina Discord server.">
                                <Flex align="center" gap={5}>
                                  <IconCircleCheckFilled
                                    color={theme.colors.patina[4]}
                                    size={18}
                                  />
                                  <Text size="sm">{user.nickname}</Text>
                                </Flex>
                              </Tooltip>
                            )}
                            {user.nickname &&
                              tagFF &&
                              user.tags &&
                              user.tags.length > 0 && (
                                <Divider orientation="vertical" h={20} />
                              )}
                            {tagFF && user.tags && user.tags.length > 0 && (
                              <TagList tags={user.tags} size={16} gap="xs" />
                            )}
                          </Flex>
                        )}
                      </Stack>
                    </Flex>
                  </Flex>
                </Flex>
              </Card>
            ))}
          </Stack>

          {/* Infinite scroll trigger */}
          {hasNextPage && (
            <Center ref={ref} py="lg">
              {isFetchingNextPage && <Loader size="sm" />}
            </Center>
          )}

          {!hasNextPage && allUsers.length > 0 && (
            <Center py="lg">
              <Text c="dimmed" size="sm">
                No more users to load
              </Text>
            </Center>
          )}
        </Box>
      )}
    </Stack>
  );
}

export function UserSearchSkeleton() {
  return (
    <Stack gap="md" data-testid="user-search-skeleton">
      <Skeleton height={36} mt="md" />
      {Array.from({ length: 5 }).map((_, i) => (
        <Skeleton key={i} height={80} radius="md" />
      ))}
    </Stack>
  );
}
