import CustomPagination from "@/app/submission/u/[userId]/_components/CustomPagination";
import { useUserSubmissionsQuery } from "@/app/submission/u/[userId]/hooks";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import {
  langNameKey,
  langNameToIcon,
} from "@/components/ui/langname-to-icon/LangNameToIcon";
import { theme } from "@/lib/theme";
import { timeDiff } from "@/lib/timeDiff";
import {
  Badge,
  Box,
  Button,
  Center,
  Flex,
  Group,
  Loader,
  Overlay,
  Table,
  Text,
  Title,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaArrowLeft, FaArrowRight, FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

const MAX_PER_PAGE = 5;

export default function UserSubmissionContent({ userId }: { userId?: string }) {
  const { data, status, page, goBack, goForward, isPlaceholderData, goTo } =
    useUserSubmissionsQuery({
      userId,
      tieToUrl: true,
    });

  if (status === "pending") {
    return (
      <>
        <Header />
        <Flex
          direction={"column"}
          align={"center"}
          justify={"center"}
          miw={"98vw"}
          mih={"90vh"}
        >
          <Loader />
        </Flex>
        <Footer />
      </>
    );
  }

  if (status === "error") {
    return (
      <>
        <Header />
        <Flex direction={"column"} align={"center"} miw={"98vw"} mih={"90vh"}>
          <Text>Sorry, something went wrong.</Text>
        </Flex>
        <Footer />
      </>
    );
  }

  if (!data.success) {
    return (
      <>
        <Header />
        <Flex direction={"column"} align={"center"} miw={"98vw"} mih={"90vh"}>
          <Text>{data.message}</Text>
        </Flex>
        <Footer />
      </>
    );
  }

  const submissions = data.data;

  if (submissions.length === 0) {
    return (
      <>
        <Header />
        <Flex
          direction={"column"}
          align={"center"}
          justify={"center"}
          miw={"98vw"}
          mih={"90vh"}
          gap={"sm"}
        >
          <Text c={"dimmed"}>Sorry, no submissions were found.</Text>
          <Button component={Link} to={"/dashboard"} variant={"outline"}>
            Go back to dashboard
          </Button>
        </Flex>
        <Footer />
      </>
    );
  }

  return (
    <>
      <Header />
      <Flex
        direction={"column"}
        align={"center"}
        miw={"98vw"}
        mih={"90vh"}
        p={"lg"}
      >
        <Flex direction={"row"} gap="xs" wrap={"wrap"}>
          <Title size="h3" ta="center">
            Latest data for
          </Title>
          <Group wrap="wrap" justify="center" gap="xs">
            {submissions[0].nickname ?
              <>
                <Tooltip
                  label={
                    "This user is a verified member of the Patina Discord server."
                  }
                  color={"dark.4"}
                >
                  <Title size="h4" c="patina.4">
                    <IconCircleCheckFilled
                      className="inline"
                      color={theme.colors.patina[4]}
                      z={5000000}
                      size={20}
                    />{" "}
                    {submissions[0].nickname}
                  </Title>
                </Tooltip>
              </>
            : <>
                <FaDiscord
                  style={{
                    color: "var(--mantine-color-blue-5)",
                    fontSize: "1.5rem",
                  }}
                />
                <Title size="h4" c="blue.5">
                  {submissions[0].discordName}
                </Title>
              </>
            }
            <Link
              to={`https://leetcode.com/u/${submissions[0].leetcodeUsername}`}
              className="hover:underline"
              style={{ display: "flex", alignItems: "center", gap: "4px" }}
            >
              <SiLeetcode
                style={{
                  color: "var(--mantine-color-yellow-5)",
                  fontSize: "1.5rem",
                }}
              />
              <Title size="h4" c="yellow.5">
                {submissions[0].leetcodeUsername}
              </Title>
            </Link>
          </Group>
        </Flex>
        <Button component={Link} to={"/dashboard"} variant={"outline"}>
          Go back to dashboard
        </Button>
        <Box style={{ overflowX: "auto" }} maw={"100%"} miw={"66%"}>
          <Table
            verticalSpacing={"lg"}
            horizontalSpacing={"xs"}
            withRowBorders={false}
            striped
            my={"sm"}
            pos={"relative"}
          >
            {isPlaceholderData && (
              <Overlay zIndex={1000} backgroundOpacity={0.35} blur={4} />
            )}
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Lang</Table.Th>
                <Table.Th>Title</Table.Th>
                <Table.Th>Difficulty</Table.Th>
                <Table.Th>Accepted</Table.Th>
                <Table.Th>Pts</Table.Th>
                <Table.Th>Solved</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {submissions.map((submission, index) => {
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
                  <Table.Tr key={index}>
                    <Table.Td>
                      <LanguageIcon size={24} width={24} height={24} />
                    </Table.Td>
                    <Table.Td>
                      <Text
                        component={Link}
                        to={`/submission/s/${submission.id}`}
                        className="transition-all hover:text-blue-500"
                      >
                        {submission.questionTitle}
                      </Text>
                    </Table.Td>
                    <Table.Td>
                      <Badge ta="center" color={badgeDifficultyColor}>
                        {submission.questionDifficulty}
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Badge ta={"center"} color={badgeAcceptedColor}>
                        {Math.round(submission.acceptanceRate * 100)}%
                      </Badge>
                    </Table.Td>
                    <Table.Td>{submission.pointsAwarded}</Table.Td>
                    <Table.Td>
                      {timeDiff(new Date(submission.submittedAt))}
                    </Table.Td>
                  </Table.Tr>
                );
              })}
              {/* Render empty values to fill up page and avoid content shifting.*/}
              {submissions.length < MAX_PER_PAGE &&
                Array(MAX_PER_PAGE - submissions.length)
                  .fill(0)
                  .map((_, idx) => (
                    <Table.Tr key={idx} opacity={0}>
                      <Table.Td>Language Icon</Table.Td>
                      <Table.Td>
                        <Text>Sample problem.</Text>
                      </Table.Td>
                      <Table.Td>
                        <Badge ta="center">Difficulty</Badge>
                      </Table.Td>
                      <Table.Td>
                        <Badge ta={"center"}>AC%</Badge>
                      </Table.Td>
                      <Table.Td>PTs</Table.Td>
                      <Table.Td>Date 1</Table.Td>
                    </Table.Tr>
                  ))}
            </Table.Tbody>
          </Table>
        </Box>
        <Center my={"sm"}>
          <Flex direction={"row"} gap={"sm"}>
            <Button disabled={page === 1} onClick={goBack} size={"compact-sm"}>
              <FaArrowLeft />
            </Button>
            <CustomPagination
              goTo={goTo}
              pages={data.pages}
              currentPage={page}
            />
            <Button
              disabled={!data.hasNextPage || page >= data.pages}
              onClick={() => {
                if (data.hasNextPage || page >= data.pages) {
                  goForward();
                }
              }}
              size={"compact-sm"}
            >
              <FaArrowRight />
            </Button>
          </Flex>
        </Center>
      </Flex>
      <Footer />
    </>
  );
}
