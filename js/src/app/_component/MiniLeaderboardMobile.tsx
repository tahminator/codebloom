import MiniLeaderboardMobileSkeleton from "@/app/_component/skeletons/MiniLeaderboardMobileSkeleton";
import LeaderboardCard from "@/components/ui/LeaderboardCard";
import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardUsersQuery } from "@/lib/api/queries/leaderboard";
import { UserTagTag } from "@/lib/api/types/autogen/schema";
import getOrdinal from "@/lib/helper/ordinal";
import { theme } from "@/lib/theme";
import {
  Button,
  Flex,
  Overlay,
  SegmentedControl,
  Text,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function MiniLeaderboardMobile() {
  const { data, status, filters, toggleFilter, isPlaceholderData } =
    useCurrentLeaderboardUsersQuery({ pageSize: 5, tieToUrl: false });

  if (status === "pending") {
    return <MiniLeaderboardMobileSkeleton />;
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const leaderboardData = data.payload;

  if (leaderboardData.items.length == 0) {
    return <p>Sorry, there are no users to display.</p>;
  }

  const [first, second, third] = leaderboardData.items;

  return (
    <>
      <SegmentedControl
        value={filters.Patina ? "patina" : "all"}
        w={"100%"}
        variant={"light"}
        data={[
          { label: "All", value: "all" },
          { label: "Patina", value: "patina" },
        ]}
        onChange={() => toggleFilter(UserTagTag.Patina)}
      />
      <div style={{ position: "relative" }}>
        {isPlaceholderData && (
          <Overlay
            zIndex={1000}
            backgroundOpacity={0.55}
            blur={10}
            radius={"md"}
          />
        )}
        <div
          className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
          style={{ marginBottom: "2rem", marginTop: "1rem" }}
        >
          {first && (
            <LeaderboardCard
              placeString={getOrdinal(first.index)}
              sizeOrder={1}
              discordName={first.discordName}
              leetcodeUsername={first.leetcodeUsername}
              nickname={first.nickname}
              totalScore={first.totalScore}
              width={"300px"}
              userId={first.id}
              isLoading={isPlaceholderData}
            />
          )}
          {second && (
            <LeaderboardCard
              placeString={getOrdinal(second.index)}
              sizeOrder={2}
              discordName={second.discordName}
              leetcodeUsername={second.leetcodeUsername}
              nickname={second.nickname}
              totalScore={second.totalScore}
              width={"300px"}
              userId={second.id}
              isLoading={isPlaceholderData}
            />
          )}
          {third && (
            <LeaderboardCard
              placeString={getOrdinal(third.index)}
              sizeOrder={3}
              discordName={third.discordName}
              leetcodeUsername={third.leetcodeUsername}
              nickname={third.nickname}
              totalScore={third.totalScore}
              width={"300px"}
              userId={third.id}
              isLoading={isPlaceholderData}
            />
          )}
        </div>
        {leaderboardData.items.length > 3 && (
          <Flex direction="column" gap="xs" mt="1rem" mb="1rem">
            {leaderboardData.items.map((entry, index) => {
              if ([0, 1, 2].includes(index)) return null;
              return (
                <Flex
                  key={entry.id}
                  bg={theme.colors.dark[7]}
                  style={{
                    borderColor: theme.colors.dark[3],
                    border: "1px solid",
                    borderRadius: "8px",
                    padding: "0.75rem 1rem",
                    transition: "all 0.2s",
                    cursor: "pointer",
                  }}
                  onClick={() => (window.location.href = `/user/${entry.id}`)}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.boxShadow =
                      "0 4px 6px rgba(0, 0, 0, 0.1)";
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.boxShadow = "none";
                  }}
                >
                  <Flex
                    justify="space-between"
                    align="center"
                    gap="sm"
                    w="100%"
                  >
                    <Flex align="center" gap="sm" miw={0}>
                      <Text
                        size="lg"
                        fw={700}
                        c={theme.colors.patina[4]}
                        miw="35px"
                        fz="16px"
                      >
                        #{entry.index}
                      </Text>
                      <Flex direction="column" gap={4} miw={0}>
                        {entry.nickname && (
                          <Flex align="center" gap={6}>
                            <Tooltip
                              label="This user is a verified member of the Patina Discord server."
                              color="dark.4"
                            >
                              <Flex align="center" gap={6}>
                                <IconCircleCheckFilled
                                  color={theme.colors.patina[4]}
                                  size={16}
                                />
                                <Text
                                  fw={600}
                                  size="sm"
                                  style={{
                                    overflow: "hidden",
                                    textOverflow: "ellipsis",
                                    whiteSpace: "nowrap",
                                  }}
                                >
                                  {entry.nickname}
                                </Text>
                              </Flex>
                            </Tooltip>
                          </Flex>
                        )}
                        <Flex gap="sm" wrap="wrap">
                          <Flex align="center" gap={4}>
                            <FaDiscord size={14} />
                            <Text
                              size="xs"
                              style={{
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                whiteSpace: "nowrap",
                                maxWidth: "120px",
                              }}
                            >
                              {entry.discordName}
                            </Text>
                          </Flex>
                          <Flex align="center" gap={4}>
                            <SiLeetcode size={14} />
                            <Text
                              size="xs"
                              style={{
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                whiteSpace: "nowrap",
                                maxWidth: "120px",
                              }}
                            >
                              {entry.leetcodeUsername}
                            </Text>
                          </Flex>
                        </Flex>
                      </Flex>
                    </Flex>
                    <Text
                      size="sm"
                      fw={600}
                      miw={70}
                      style={{ textAlign: "right" }}
                    >
                      {entry.totalScore} Pts
                    </Text>
                  </Flex>
                </Flex>
              );
            })}
          </Flex>
        )}
        <Button
          variant={"light"}
          w={"100%"}
          component={Link}
          to={`/leaderboard?patina=${filters.Patina}`}
        >
          View all
        </Button>
      </div>
    </>
  );
}
