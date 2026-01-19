import {
  langNameKey,
  langNameToIcon,
} from "@/components/ui/langname-to-icon/LangNameToIcon";
import { Api } from "@/lib/api/types";
import { ApiUtils } from "@/lib/api/utils";
import {
  Flex,
  Card,
  Stack,
  Text,
  Button,
  Avatar,
  Modal,
  Divider,
  Badge,
  Paper,
  Box,
  ScrollArea,
  Group,
  CopyButton,
} from "@mantine/core";
import { useState } from "react";

type DuelData = Api<"DuelData">;
type User = Api<"UserDto">;
type Problems = {
  id: string;
  questionTitle: string;
  questionDifficulty: string;
  acceptanceRate: number;
  language?: string;
  topics?: Array<{ id: string; topic: unknown }>;
};

const getPlayerDisplayName = (player: User | null): string => {
  return player?.leetcodeUsername || "Player";
};

const getPlayerAvatar = (player: User | null): string => {
  if (!player) return "?";
  if (player.leetcodeUsername) return player.leetcodeUsername[0].toUpperCase();
  if (player.nickname) return player.nickname[0].toUpperCase();
  return player.discordName?.[0]?.toUpperCase() ?? "?";
};

export default function DuelActiveBody({
  duelData,
  currentUser = null,
  playerOneSolved = [],
  playerTwoSolved = [],
}: {
  duelData: DuelData;
  currentUser?: User | null;
  playerOneSolved?: Problems[];
  playerTwoSolved?: Problems[];
}) {
  const { players, questions, lobby } = duelData;
  const [showForfeit, setShowForfeit] = useState(false);

  const [playerOne, playerTwo] = players;

  const isCurrentPlayerOne = !!(
    currentUser &&
    playerOne &&
    currentUser.id === playerOne.id
  );
  const isCurrentPlayerTwo = !!(
    currentUser &&
    playerTwo &&
    currentUser.id === playerTwo.id
  );

  // Replace with actual time calculation from duelData.lobby.expiresAt
  const mockTimeLeft = "29:45"; // Format: MM:SS

  return (
    <>
      <Box pos="fixed" top={20} right={20}>
        <CopyButton value={lobby.joinCode}>
          {({ copied, copy }) => (
            <Button
              size="md"
              variant="filled"
              onClick={copy}
              color={"dark.7"}
              bdrs={"md"}
            >
              <Group>
                <Text fw={700} ff="monospace" c="white" size="lg">
                  {lobby.joinCode}
                </Text>
                <Badge
                  color={copied ? "teal" : undefined}
                  variant="filled"
                  size="md"
                >
                  {copied ? "✓ Copied" : "Copy"}
                </Badge>
              </Group>
            </Button>
          )}
        </CopyButton>
      </Box>
      <Box pos="fixed" top={20} left={20}>
        <Card withBorder bg="dark.8">
          <Text size="xl" fw={700} ta="center" c="white">
            {mockTimeLeft}
          </Text>
        </Card>
      </Box>
      <Flex gap="lg" w="100%" h="85vh" justify="center" align="center">
        <Stack>
          <PlayerCard player={playerOne} />
          {isCurrentPlayerOne && (
            <>
              <Button size="sm" onClick={() => {}}>
                Submit
              </Button>
              <Button
                color="red"
                size="sm"
                onClick={() => setShowForfeit(true)}
              >
                Forfeit
              </Button>
            </>
          )}
        </Stack>
        <Card p="md" withBorder w={400} h="80vh">
          <Stack gap={0} h="100%">
            <PlayerSolvedSection player={playerOne} solved={playerOneSolved} />
            <Divider my="md" />
            <QuestionsSection questions={questions} />
            <Divider my="md" />
            <PlayerSolvedSection player={playerTwo} solved={playerTwoSolved} />
          </Stack>
        </Card>
        <Stack>
          <PlayerCard player={playerTwo} />
          {isCurrentPlayerTwo && (
            <>
              <Button size="sm" onClick={() => {}}>
                Submit
              </Button>
              <Button
                color="red"
                size="sm"
                onClick={() => setShowForfeit(true)}
              >
                Forfeit
              </Button>
            </>
          )}
        </Stack>
      </Flex>
      <ForfeitModal
        opened={showForfeit}
        onClose={() => setShowForfeit(false)}
        onConfirm={() => setShowForfeit(false)}
      />
    </>
  );
}

function PlayerCard({ player }: { player: User | null }) {
  return (
    <Card radius="md" withBorder p="md" w={175} h={175}>
      <Flex
        direction="column"
        align="center"
        justify="center"
        h="100%"
        gap="sm"
      >
        <Avatar size={70}>{getPlayerAvatar(player)}</Avatar>
        <Text size="sm" fw={600} ta="center">
          {getPlayerDisplayName(player)}
        </Text>
      </Flex>
    </Card>
  );
}

function PlayerSolvedSection({
  player,
  solved = [],
}: {
  player: User | null;
  solved?: Problems[];
}) {
  return (
    <Box pb="md" flex={1} style={{ overflow: "hidden" }}>
      <Text size="sm" fw={600} mb="xs" ta="center">
        {getPlayerDisplayName(player)}'s problem(s) solved
      </Text>
      <ScrollArea h="100%">
        <Stack gap="xs">
          {solved.length > 0 ?
            solved.map((question) => (
              <QuestionCard key={question.id} question={question} />
            ))
          : null}
        </Stack>
      </ScrollArea>
    </Box>
  );
}

function QuestionsSection({ questions }: { questions?: Problems[] }) {
  return (
    <Box flex={1}>
      <Text size="sm" fw={600} mb="xs" ta="center">
        Available
      </Text>
      <Stack gap="xs">
        {questions && questions.length > 0 ?
          questions.map((question) => (
            <QuestionCard key={question.id} question={question} />
          ))
        : null}
      </Stack>
    </Box>
  );
}

function ForfeitModal({
  opened,
  onClose,
  onConfirm,
}: {
  opened: boolean;
  onClose: () => void;
  onConfirm: () => void;
}) {
  return (
    <Modal opened={opened} onClose={onClose} centered withCloseButton={false}>
      <Stack gap="lg">
        <Text size="md" fw={700} ta="center">
          Are you sure you want to forfeit the match?
        </Text>
        <Group justify="center" gap="lg">
          <Button onClick={onClose} w={150}>
            X
          </Button>
          <Button color="red" onClick={onConfirm} w={150}>
            ✓
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
}

function QuestionCard({ question }: { question: Problems }) {
  const getTopicName = (t: unknown): string => {
    const fn = ApiUtils.getTopicEnumMetadataByTopicEnum as unknown as (
      x: unknown,
    ) => { name: string };
    return fn(t).name;
  };

  const getDifficultyColor = (difficulty: string) => {
    const difficultyMap: Record<string, string | undefined> = {
      Easy: undefined,
      Medium: "yellow",
      Hard: "red",
    };
    return difficultyMap[difficulty];
  };

  const getAcceptanceColor = (rate: number) => {
    const percentage = rate * 100;
    if (percentage >= 75) return undefined;
    if (percentage >= 50) return "yellow";
    return "red";
  };

  const LanguageIcon =
    langNameToIcon[question.language as langNameKey] ||
    langNameToIcon["default"];

  return (
    <Paper withBorder p="sm" radius="md" bg="dark.7">
      <Stack gap="xs">
        <Group justify="space-between" align="flex-start">
          <Group gap="xs" flex={1} miw={0}>
            <LanguageIcon size={18} width={18} height={18} />
            <Flex direction="column" flex={1} miw={0}>
              <Text size="sm" fw={500} lineClamp={2}>
                {question.questionTitle}
              </Text>
            </Flex>
          </Group>
        </Group>
        <Group justify="space-between" wrap="wrap" gap="xs">
          <Group gap="xs">
            <Badge
              size="sm"
              color={getDifficultyColor(question.questionDifficulty)}
            >
              {question.questionDifficulty}
            </Badge>
            <Badge
              size="sm"
              color={getAcceptanceColor(question.acceptanceRate)}
            >
              {Math.round(question.acceptanceRate * 100)}%
            </Badge>
          </Group>
        </Group>
        {question.topics && question.topics.length > 0 && (
          <Group gap="xs" wrap="wrap">
            {question.topics.map((topic) => (
              <Badge key={topic.id} size="xs" variant="filled" color="gray.4">
                {getTopicName(topic.topic)}
              </Badge>
            ))}
          </Group>
        )}
      </Stack>
    </Paper>
  );
}
