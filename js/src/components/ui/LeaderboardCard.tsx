import { UserTag } from "@/lib/api/types/usertag";
import { tagFF } from "@/lib/ff";
import { OrdinalString } from "@/lib/helper/ordinal";
import { theme } from "@/lib/theme";
import {
  Card,
  Text,
  Tooltip,
  Flex,
  LoadingOverlay,
  Stack,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { CSSProperties } from "react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

import TagList from "./tags/TagList";

export default function LeaderboardCard({
  placeString,
  discordName,
  leetcodeUsername,
  sizeOrder,
  totalScore,
  width,
  userId,
  nickname,
  tags,
  isLoading = false,
}: {
  placeString: OrdinalString;
  sizeOrder: 1 | 2 | 3;
  discordName: string;
  leetcodeUsername: string | null;
  totalScore: number;
  width: CSSProperties["width"];
  userId: string;
  nickname: string | null;
  tags?: UserTag[];
  isLoading?: boolean;
}) {
  const isTopThree = sizeOrder <= 3;
  const borderColor = (() => {
    if (placeString === "1st") return "!border-yellow-400";
    if (placeString === "2nd") return "!border-slate-300";
    if (placeString === "3rd") return "!border-amber-700";
    return "";
  })();

  const boxShadow = (() => {
    if (placeString === "1st")
      return "0 0 15px rgba(250, 204, 21, 0.4), 0 0 30px rgba(250, 204, 21, 0.2), 0 0 45px rgba(34, 197, 94, 0.1)";
    if (placeString === "2nd")
      return "0 0 12px rgba(203, 213, 225, 0.35), 0 0 25px rgba(148, 163, 184, 0.2)";
    if (placeString === "3rd")
      return "0 0 12px rgba(217, 119, 6, 0.25), 0 0 25px rgba(217, 119, 6, 0.12)";
    return undefined;
  })();

  const height = (() => {
    switch (sizeOrder) {
      case 1:
        return "225px";
      case 2:
        return "215px";
      case 3:
        return "210px";
    }
  })();
  const displayTags = tags?.slice(0, 3);

  return (
    <Card
      withBorder
      shadow="sm"
      radius="md"
      className={`${isTopThree ? `border-4 ${borderColor}` : "border-2"} flex flex-col items-center justify-between relative`}
      style={{
        height,
        width,
        padding: "1.25rem 1rem",
        boxShadow,
      }}
      component={Link}
      to={`/user/${userId}`}
    >
      <LoadingOverlay visible={isLoading} />
      <Text ta="center" size="xl" fw={isTopThree ? 800 : 500}>
        {placeString}
      </Text>
      <Stack gap={4} align="center" my="auto">
        {nickname && (
          <Tooltip
            label={
              "This user is a verified member of the Patina Discord server."
            }
            color={"dark.4"}
          >
            <Text
              ta="center"
              fw={isTopThree ? 700 : 600}
              truncate
              style={{
                fontSize: `clamp(1rem, ${100 / (nickname.length + 5)}vw, 1.25rem)`,
              }}
            >
              <IconCircleCheckFilled
                className="inline"
                color={theme.colors.patina[4]}
              />{" "}
              {nickname}
            </Text>
          </Tooltip>
        )}
        <Flex align="center" justify="center" gap="xs">
          <Text
            ta="center"
            fw={isTopThree ? 600 : 500}
            truncate
            style={{
              fontSize: `clamp(0.9rem, ${100 / (discordName.length + 5)}vw, 1.1rem)`,
            }}
          >
            <FaDiscord className="inline" /> {discordName}
          </Text>
          {tagFF && displayTags && displayTags.length > 0 && (
            <TagList
              tags={displayTags}
              size={14}
              gap="xs"
            />
          )}
        </Flex>
        <Text
          ta="center"
          fw={isTopThree ? 500 : 400}
          style={{ fontSize: "0.95rem" }}
        >
          <SiLeetcode className="inline" /> {leetcodeUsername}
        </Text>
      </Stack>
      <Text ta="center" fw={isTopThree ? 700 : 500} size="lg">
        {totalScore} Points
      </Text>
    </Card>
  );
}
