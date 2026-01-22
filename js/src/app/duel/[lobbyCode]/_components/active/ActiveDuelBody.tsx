import transition from "@/app/duel/[lobbyCode]/_components/active/ActiveDuelBody.module.css";
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
  Pagination,
  ActionIcon,
} from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";
import { IconMaximize } from "@tabler/icons-react";
import { useMemo, useState } from "react";

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

type SectionKey = "player1" | "available" | "player2";

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
  const isMobile = useMediaQuery("(max-width: 768px)");
  const [expanded, setExpanded] = useState<SectionKey | null>(null);
  const [activePage, setActivePage] = useState(1);

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

  // TODO: Replace with actual time calculation from duelData.lobby.expiresAt
  const mockTimeLeft = "29:45"; // Format: MM:SS

  const createSections = (hideTitle = false, compact = false) => [
    {
      key: "player1" as const,
      title: getPlayerDisplayName(playerOne),
      content: (
        <PlayerSolvedSection
          player={playerOne}
          solved={playerOneSolved}
          showControls={isCurrentPlayerOne}
          compact={compact}
          hideTitle={hideTitle}
        />
      ),
    },
    {
      key: "available" as const,
      title: "Available",
      content: (
        <QuestionsSection
          questions={questions}
          compact={compact}
          hideTitle={hideTitle}
        />
      ),
    },
    {
      key: "player2" as const,
      title: getPlayerDisplayName(playerTwo),
      content: (
        <PlayerSolvedSection
          player={playerTwo}
          solved={playerTwoSolved}
          showControls={isCurrentPlayerTwo}
          compact={compact}
          hideTitle={hideTitle}
        />
      ),
    },
  ];

  const mobileSections = useMemo(
    () => createSections(true, false),
    [
      playerOne,
      playerTwo,
      playerOneSolved,
      playerTwoSolved,
      questions,
      isCurrentPlayerOne,
      isCurrentPlayerTwo,
    ],
  );

  const copyJoinCodeButton = () => (
    <CopyButton value={lobby.joinCode}>
      {({ copied, copy }) => (
        <Button
          size="md"
          variant="filled"
          onClick={copy}
          color="dark.7"
          radius="md"
        >
          <Group gap="xs">
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
  );

  const countdownTimer = () => (
    <Card withBorder bg="dark.8" p="xs">
      <Text
        size="xl"
        fw={650}
        ta="center"
        c="white"
        w={isMobile ? 300 : undefined}
      >
        {mockTimeLeft}
      </Text>
    </Card>
  );

  const playerControls = (isPlayer: boolean) =>
    isPlayer && (
      <>
        <Button w={165} size="sm" onClick={() => {}}>
          Submit
        </Button>
        <Button
          color="red"
          w={165}
          size="sm"
          onClick={() => setShowForfeit(true)}
        >
          Forfeit
        </Button>
      </>
    );

  if (isMobile) {
    return (
      <>
        <Flex direction="column" h="85vh" w="100%">
          <Box flex={1} w="100%" px="md" py="md">
            {activePage === 1 && (
              <Stack gap="lg" h="100%" align="center" justify="center">
                {copyJoinCodeButton()}
                <Group gap="xl" align="center" wrap="nowrap">
                  <PlayerCard player={playerOne} />
                  <PlayerCard player={playerTwo} />
                </Group>
                {(isCurrentPlayerOne || isCurrentPlayerTwo) && (
                  <Button
                    color="red"
                    size="sm"
                    onClick={() => setShowForfeit(true)}
                    w={150}
                  >
                    Forfeit
                  </Button>
                )}
              </Stack>
            )}
            {activePage === 2 && (
              <Flex direction="column" h="85vh" align="center" gap="md">
                {countdownTimer()}
                <Card p="md" withBorder w={370} h="80vh">
                  <Stack gap={0} h="100%">
                    {mobileSections.map((section, idx, arr) => {
                      const isPlayerSection =
                        section.key === "player1" || section.key === "player2";
                      const isExpanded = expanded === section.key;
                      const isHidden = expanded && !isExpanded;

                      return (
                        <Box
                          key={section.key}
                          flex={
                            expanded ?
                              isExpanded ?
                                1
                              : 0
                            : isPlayerSection ?
                              "1 1 0"
                            : "0 0 auto"
                          }
                          pb={idx < arr.length - 1 && !expanded ? "md" : 0}
                          className={`${transition.sectionBox} ${isHidden ? transition.sectionBoxHidden : ""}`}
                        >
                          <Box
                            className={`${transition.sectionContent} ${
                              isExpanded || (isPlayerSection && !expanded) ?
                                transition.sectionContentScrollable
                              : transition.sectionContentVisible
                            }`}
                          >
                            <Group justify="space-between" mb="xs">
                              <Text size="sm" fw={600}>
                                {section.title}
                              </Text>
                              <ActionIcon
                                variant="subtle"
                                onClick={() =>
                                  setExpanded((prev) =>
                                    prev === section.key ? null : section.key,
                                  )
                                }
                                className={`${transition.expandIcon} ${isExpanded ? transition.expandIconRotated : ""}`}
                              >
                                <IconMaximize size={18} />
                              </ActionIcon>
                            </Group>
                            <Box
                              style={{
                                overflowY: "inherit",
                              }}
                            >
                              {section.content}
                            </Box>
                          </Box>
                          {idx < arr.length - 1 && !expanded && (
                            <Divider
                              mt="md"
                              className={`${transition.divider} ${isHidden ? transition.dividerHidden : ""}`}
                            />
                          )}
                        </Box>
                      );
                    })}
                  </Stack>
                </Card>
                {(isCurrentPlayerOne || isCurrentPlayerTwo) && (
                  <Stack gap="sm" align="center">
                    <Button size="md" onClick={() => {}} w={200}>
                      Submit
                    </Button>
                  </Stack>
                )}
              </Flex>
            )}
            <Flex justify="center" py="md">
              <Pagination
                total={2}
                value={activePage}
                onChange={setActivePage}
                size="md"
              />
            </Flex>
          </Box>
        </Flex>
        <ForfeitModal
          opened={showForfeit}
          onClose={() => setShowForfeit(false)}
          onConfirm={() => setShowForfeit(false)}
        />
      </>
    );
  }

  return (
    <>
      <Box pos="fixed" top={20} right={20}>
        {copyJoinCodeButton()}
      </Box>
      <Box pos="fixed" top={20} left={20}>
        <Card withBorder bg="dark.8">
          <Text size="xl" fw={700} ta="center" c="white">
            {mockTimeLeft}
          </Text>
        </Card>
      </Box>
      <Flex gap="lg" w="100%" h="86vh" justify="center" align="center">
        <Flex
          direction="column"
          align="center"
          gap="md"
          mt={isCurrentPlayerTwo ? -104 : 0}
        >
          <PlayerCard player={playerOne} />
          {playerControls(isCurrentPlayerOne)}
        </Flex>
        <Card p="md" withBorder w={400} h="92vh">
          <Stack gap={0} h="100%">
            <PlayerSolvedSection player={playerOne} solved={playerOneSolved} />
            <Divider my="md" />
            <QuestionsSection questions={questions} />
            <Divider my="md" />
            <PlayerSolvedSection player={playerTwo} solved={playerTwoSolved} />
          </Stack>
        </Card>
        <Flex
          direction="column"
          align="center"
          gap="md"
          mt={isCurrentPlayerTwo ? 0 : -104}
        >
          <PlayerCard player={playerTwo} />
          {playerControls(isCurrentPlayerTwo)}
        </Flex>
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
  const isMobile = useMediaQuery("(max-width: 768px)");
  const size = isMobile ? 160 : 175;
  const avatarSize = isMobile ? 75 : 70;

  return (
    <Card radius="md" withBorder p="md" w={size} h={size}>
      <Flex
        direction="column"
        align="center"
        justify="center"
        h="100%"
        gap="sm"
      >
        <Avatar size={avatarSize}>{getPlayerAvatar(player)}</Avatar>
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
  showControls = false,
  compact = false,
  hideTitle = false,
}: {
  player: User | null;
  solved?: Problems[];
  showControls?: boolean;
  compact?: boolean;
  hideTitle?: boolean;
}) {
  const emptyMessage = (
    <Text size="sm" c="dimmed">
      No solved problems yet
    </Text>
  );
  const problemsList =
    solved.length > 0 ?
      solved.map((q) => <QuestionCard key={q.id} question={q} />)
    : emptyMessage;

  if (compact) {
    return (
      <Stack gap="sm">
        <Group align="center" gap="sm">
          <Avatar size={48}>{getPlayerAvatar(player)}</Avatar>
          <Text fw={600}>{getPlayerDisplayName(player)}</Text>
        </Group>
        <ScrollArea h={180} type="always" offsetScrollbars>
          <Stack gap={6}>{problemsList}</Stack>
        </ScrollArea>
        {showControls && (
          <Group grow>
            <Button color="red" variant="light" radius="md">
              Forfeit
            </Button>
          </Group>
        )}
      </Stack>
    );
  }

  return (
    <Box pb="md" flex={1} style={{ overflow: "hidden" }}>
      {!hideTitle && (
        <Text size="sm" fw={600} mb="xs" ta="center">
          {getPlayerDisplayName(player)}
        </Text>
      )}
      <ScrollArea h="100%">
        <Stack gap={6}>{problemsList}</Stack>
      </ScrollArea>
    </Box>
  );
}

function QuestionsSection({
  questions = [],
  compact = false,
  hideTitle = false,
}: {
  questions?: Problems[];
  compact?: boolean;
  hideTitle?: boolean;
}) {
  const emptyMessage = (
    <Text size="sm" c="dimmed">
      No available questions
    </Text>
  );
  const questionsList =
    questions.length > 0 ?
      questions.map((q) => <QuestionCard key={q.id} question={q} />)
    : emptyMessage;

  if (compact) {
    return (
      <Stack gap="sm">
        <ScrollArea h={220} type="always" offsetScrollbars>
          <Stack gap={6}>{questionsList}</Stack>
        </ScrollArea>
      </Stack>
    );
  }

  return (
    <Box flex={1}>
      {!hideTitle && (
        <Text size="sm" fw={600} mb="xs" ta="center">
          Available
        </Text>
      )}
      <Stack gap={6}>{questionsList}</Stack>
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

function QuestionCard({ question }: { question: Problems }) {
  const getTopicName = (t: unknown): string => {
    const fn = ApiUtils.getTopicEnumMetadataByTopicEnum as unknown as (
      x: unknown,
    ) => { name: string };
    return fn(t).name;
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
