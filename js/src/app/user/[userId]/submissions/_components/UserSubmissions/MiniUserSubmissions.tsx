import {
  langNameKey,
  langNameToIcon,
} from "@/components/ui/langname-to-icon/LangNameToIcon";
import Toast from "@/components/ui/toast/Toast";
import { useUserSubmissionsQuery } from "@/lib/api/queries/user";
import { timeDiff } from "@/lib/timeDiff";
import {
  Badge,
  Box,
  Overlay,
  Text,
  Group,
  Card,
  Stack,
} from "@mantine/core";
import { Link } from "react-router-dom";

import MiniUserSubmissionsSkeleton from "./MiniUserSubmissionsSkeleton";

const formatTopicName = (topicSlug: string) => {
  return topicSlug
    .split("-")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
};

export default function MiniUserSubmissions({ userId }: { userId?: string }) {
  const { data, status, isPlaceholderData } = useUserSubmissionsQuery({
    userId,
    tieToUrl: true,
    pageSize: 5,
  });

  if (status === "pending") {
    return (
      <>
        <MiniUserSubmissionsSkeleton />
      </>
    );
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong when trying to fetch user's submissions. Please try again later." />
    );
  }

  if (!data.success) {
    return <>{data.message}</>;
  }

  const pageData = data.payload;
  return (
    <Box pos="relative" px="xs">
      {isPlaceholderData && <Overlay zIndex={1000} opacity={0.35} blur={4} />}
      <Stack gap="sm" my="sm">
        {pageData.items.length === 0 && (
          <>
            <Card
              withBorder
              p="md"
              radius="md"
              style={{ minHeight: "80px", width: "100%", flex: 1 }}
            >
              <Stack gap="sm" justify="center" align="center" h="100%">
                <Text fw={500} ta="center" c="dimmed">
                  Nothing found.
                </Text>
                <Text size="sm" ta="center" c="dimmed">
                  No submissions has been entered yet.
                </Text>
              </Stack>
            </Card>
          </>
        )}
        {pageData.items.map((submission, index) => {
          const badgeDifficultyColor = (() => {
            if (submission.questionDifficulty === "Easy") {
              return undefined;
            }
            if (submission.questionDifficulty === "Medium") {
              return "yellow";
            }
            if (submission.questionDifficulty === "Hard") {
              return "red";
            }
            return undefined;
          })();
          const badgeAcceptedColor = (() => {
            const acceptanceRate = submission.acceptanceRate * 100;
            if (acceptanceRate >= 75) {
              return undefined;
            }
            if (acceptanceRate >= 50) {
              return "yellow";
            }
            if (acceptanceRate >= 0) {
              return "red";
            }
            return undefined;
          })();
          const LanguageIcon =
            langNameToIcon[submission.language as langNameKey] ||
            langNameToIcon["default"];
          return (
            <Card key={index} withBorder p="sm" radius="md">
              <Stack gap="xs">
                <Group justify="space-between" align="flex-start">
                  <Group gap="xs" style={{ flex: 1, minWidth: 0 }}>
                    <LanguageIcon size={20} width={20} height={20} />
                    <Text
                      component={Link}
                      to={`/submission/${submission.id}`}
                      className="transition-all hover:text-blue-500"
                      size="sm"
                      fw={500}
                      style={{
                        flex: 1,
                        wordBreak: "break-word",
                        lineHeight: 1.3,
                      }}
                    >
                      {submission.questionTitle}
                    </Text>
                  </Group>
                  <Text size="xs" c="dimmed" style={{ flexShrink: 0 }}>
                    {timeDiff(new Date(submission.submittedAt))}
                  </Text>
                </Group>
                <Group justify="space-between" wrap="wrap" gap="xs">
                  <Stack gap="xs">
                    <Badge size="sm" color={badgeDifficultyColor}>
                      {submission.questionDifficulty}
                    </Badge>
                    <Badge size="sm" color={badgeAcceptedColor}>
                      {Math.round(submission.acceptanceRate * 100)}%
                    </Badge>
                  </Stack>
                </Group>
                {submission.topics && submission.topics.length > 0 && (
                  <Group justify="space-between">
                    <Group gap="xs" wrap="wrap">
                      {submission.topics.map((topic) => (
                        <Badge
                          key={topic.id}
                          size="xs"
                          variant="outline"
                          color="gray"
                        >
                          {formatTopicName(topic.topicSlug)}
                        </Badge>
                      ))}
                    </Group>
                    <Text size="sm" fw={500}>
                      {submission.pointsAwarded} pts
                    </Text>
                  </Group>
                )}
              </Stack>
            </Card>
          );
        })}
      </Stack>
    </Box>
  );
}
