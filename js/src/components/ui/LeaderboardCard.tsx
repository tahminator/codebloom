import { Card, Text } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function LeaderboardCard({
  placeString,
  discordName,
  leetcodeUsername,
  totalScore,
}: {
  placeString: "First" | "Second" | "Third";
  discordName: string;
  leetcodeUsername: string;
  totalScore: number;
}) {
  const borderColor = (() => {
    if (placeString === "First") return "border-yellow-300";
    if (placeString === "Second") return "border-gray-400";
    if (placeString === "Third") return "border-yellow-800";
  })();

  const height = (() => {
    if (placeString === "First") return "180px";
    if (placeString === "Second") return "160px";
    if (placeString === "Third") return "140px";
  })();

  return (
    <Card
      withBorder
      shadow="sm"
      radius="md"
      className={`border-2 flex flex-col items-center justify-center ${borderColor}`}
      style={{
        height,
        width: "210px",
      }}
    >
      <Text ta="center" size="xl">
        {placeString}
      </Text>
      <Text ta="center" fw={700} size="lg" style={{ whiteSpace: "nowrap" }}>
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
