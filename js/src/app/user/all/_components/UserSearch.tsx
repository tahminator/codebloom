import UserSearchSkeleton from "@/app/user/all/_components/UserSearchSkeleton";
import TagList from "@/components/ui/tags/TagList";
import Toast from "@/components/ui/toast/Toast";
import { useGetAllUsersInfiniteQuery } from "@/lib/api/queries/user";
import { tagFF } from "@/lib/ff";
import { theme } from "@/lib/theme";
import {
  Box,
  Center,
  Divider,
  Flex,
  Loader,
  Paper,
  ScrollArea,
  Text,
  TextInput,
  Tooltip,
} from "@mantine/core";
import { useIntersection } from "@mantine/hooks";
import { IconCircleCheckFilled, IconSearch } from "@tabler/icons-react";
import { motion } from "motion/react";
import { useEffect, useRef, useState } from "react";
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

  const viewportRef = useRef<HTMLDivElement>(null);
  const [isFocused, setIsFocused] = useState(false);

  const { ref, entry } = useIntersection({
    root: viewportRef.current,
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
  const isActive = isFocused || searchQuery.length > 0;

  return (
    <Flex
      direction="column"
      align="center"
      p="md"
      mih="70vh"
      style={{ position: "relative" }}
    >
      <motion.div
        initial={false}
        animate={{
          y: isActive ? 0 : "calc(35vh - 56px)",
        }}
        transition={{
          type: "spring",
          stiffness: 500,
          damping: 35,
        }}
        style={{
          position: isActive ? "sticky" : "relative",
          top: isActive ? 90 : undefined,
          width: "100%",
          maxWidth: 900,
          zIndex: 99,
        }}
      >
        <TextInput
          value={searchQuery}
          onChange={(event) => setSearchQuery(event.currentTarget.value)}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          placeholder="Search for a user..."
          leftSection={<IconSearch size={24} />}
          size="xl"
          w="100%"
          radius="md"
          styles={{
            input: {
              backgroundColor: theme.colors.dark[6],
              borderColor: theme.colors.dark[4],
              height: 56,
              fontSize: 18,
              borderBottomLeftRadius: showResults ? 0 : undefined,
              borderBottomRightRadius: showResults ? 0 : undefined,
              "&:focus": {
                borderColor: theme.colors.patina[4],
              },
            },
          }}
        />
        {showResults && (
          <Paper
            shadow="lg"
            pos="absolute"
            top="100%"
            left={0}
            right={0}
            bg={theme.colors.dark[6]}
            radius="0 0 8px 8px"
            style={{
              border: `1px solid ${theme.colors.dark[4]}`,
              borderTop: "none",
              overflow: "hidden",
            }}
          >
            {status === "pending" && <UserSearchSkeleton />}
            {status === "success" && (
              <ScrollArea.Autosize mah={500} viewportRef={viewportRef}>
                {allUsers.length === 0 && (
                  <Box p="md">
                    <Text c="dimmed" ta="center">
                      No users found matching "{debouncedQuery}"
                    </Text>
                  </Box>
                )}
                {allUsers.map((user, index) => (
                  <Box
                    key={user.id}
                    component={Link}
                    to={`/user/${user.id}`}
                    p="md"
                    style={{
                      cursor: "pointer",
                      textDecoration: "none",
                      color: "inherit",
                      display: "block",
                      borderTop:
                        index > 0 ?
                          `1px solid ${theme.colors.dark[5]}`
                        : undefined,
                      transition: "background-color 0.15s ease",
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.backgroundColor =
                        theme.colors.dark[5];
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.backgroundColor = "transparent";
                    }}
                  >
                    <Flex direction="column" gap={6}>
                      <Flex direction="row" gap="md" align="center" wrap="wrap">
                        <Flex align="center" gap={8}>
                          <FaDiscord size={18} />
                          <Text size="md" fw={600}>
                            {user.discordName}
                          </Text>
                        </Flex>
                        {user.leetcodeUsername && (
                          <Flex align="center" gap={8}>
                            <SiLeetcode size={18} />
                            <Text size="md" fw={600}>
                              {user.leetcodeUsername}
                            </Text>
                          </Flex>
                        )}
                      </Flex>
                      {(user.nickname ||
                        (tagFF && user.tags && user.tags.length > 0)) && (
                        <Flex align="center" gap={6}>
                          {user.nickname && (
                            <Tooltip label="This user is a verified member of the Patina Discord server.">
                              <Flex align="center" gap={6}>
                                <IconCircleCheckFilled
                                  color={theme.colors.patina[4]}
                                  size={16}
                                />
                                <Text size="sm" c="dimmed">
                                  {user.nickname}
                                </Text>
                              </Flex>
                            </Tooltip>
                          )}
                          {user.nickname &&
                            tagFF &&
                            user.tags &&
                            user.tags.length > 0 && (
                              <Divider orientation="vertical" h={18} />
                            )}
                          {tagFF && user.tags && user.tags.length > 0 && (
                            <TagList tags={user.tags} size={16} gap="xs" />
                          )}
                        </Flex>
                      )}
                    </Flex>
                  </Box>
                ))}
                {hasNextPage && (
                  <Center ref={ref} py="sm">
                    {isFetchingNextPage && <Loader size="xs" />}
                  </Center>
                )}
                {!hasNextPage && allUsers.length > 0 && (
                  <Center py="sm">
                    <Text c="dimmed" size="xs">
                      No more users to load
                    </Text>
                  </Center>
                )}
              </ScrollArea.Autosize>
            )}
          </Paper>
        )}
      </motion.div>
    </Flex>
  );
}
