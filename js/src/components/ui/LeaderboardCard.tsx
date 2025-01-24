import { Card, Text } from "@mantine/core";
import { CSSProperties } from "react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function LeaderboardCard({
  placeString,
  discordName,
  leetcodeUsername,
  totalScore,
  width,
  userId,
}: {
  placeString: "First" | "Second" | "Third";
  discordName: string;
  leetcodeUsername: string;
  totalScore: number;
  width: CSSProperties["width"];
  userId: string;
}) {
  const borderColor = (() => {
    if (placeString === "First") return "border-yellow-300";
    if (placeString === "Second") return "border-gray-400";
    if (placeString === "Third") return "border-yellow-800";
  })();

  const height = (() => {
    if (placeString === "First") return "210px";
    if (placeString === "Second") return "185px";
    if (placeString === "Third") return "170px";
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
      to={`/submission/u/${userId}`}
    >
      <Text ta="center" size="xl">
        {placeString}
      </Text>
      <Text
        ta="center"
        fw={700}
        style={{
          width: "100%",
          fontSize: `clamp(1rem, ${100 / (discordName.length + 5)}vw, 1.25rem)`,
          whiteSpace: "nowrap",
          overflow: "hidden",
          textOverflow: "ellipsis",
        }}
      >
        <FaDiscord className="inline" /> {discordName}
      </Text>
      <Text ta="center" style={{ whiteSpace: "nowrap" }}>
        <SiLeetcode className="inline" /> {leetcodeUsername}
      </Text>
      <Text ta="center" fw={500} size="md">
        {totalScore} Points
      </Text>
    </Card>
  );
}
