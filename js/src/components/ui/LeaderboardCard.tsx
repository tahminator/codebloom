import { UserTag } from "@/lib/api/types/usertag";
import { tagFF } from "@/lib/ff";
import { OrdinalString } from "@/lib/helper/ordinal";
import { theme } from "@/lib/theme";
import { Card, Text, Tooltip, Flex } from "@mantine/core";
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
}) {
  const borderColor = (() => {
    if (placeString === "1st") return "border-yellow-300";
    if (placeString === "2nd") return "border-gray-400";
    if (placeString === "3rd") return "border-yellow-800";
  })();

  const height = (() => {
    switch (sizeOrder) {
      case 1:
        return "220px";
      case 2:
        return "200px";
      case 3:
        return "185px";
    }
  })();

  return (
    <Card
      withBorder
      shadow="sm"
      radius="md"
      className={`border-2 flex flex-col items-center justify-center ${borderColor}`}
      style={{
        height,
        width,
      }}
      component={Link}
      to={`/user/${userId}`}
    >
      <Text ta="center" size="xl">
        {placeString}
      </Text>
      {nickname && (
        <Tooltip
          label={"This user is a verified member of the Patina Discord server."}
          color={"dark.4"}
        >
          <Text
            ta="center"
            fw={700}
            style={{
              fontSize: `clamp(1rem, ${100 / (nickname.length + 5)}vw, 1.25rem)`,
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
            }}
          >
            <IconCircleCheckFilled
              className="inline"
              color={theme.colors.patina[4]}
              z={5000000}
            />{" "}
            {nickname}
          </Text>
        </Tooltip>
      )}
      <Flex align="center" gap="xs" justify="center">
        <Text
          ta="center"
          fw={700}
          style={{
            fontSize: `clamp(1rem, ${100 / (discordName.length + 5)}vw, 1.25rem)`,
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
          }}
        >
          <FaDiscord className="inline" /> {discordName}
        </Text>
        {tagFF && tags && <TagList tags={tags} size={14} gap="xs" />}
      </Flex>
      <Text ta="center" style={{ whiteSpace: "nowrap" }}>
        <SiLeetcode className="inline" /> {leetcodeUsername}
      </Text>
      <Text ta="center" fw={500} size="md">
        {totalScore} Points
      </Text>
    </Card>
  );
}
