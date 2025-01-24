import { useUserSubmissionsQuery } from "@/app/submission/u/[userId]/hooks";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import {
  Badge,
  Button,
  Center,
  Flex,
  Loader,
  Table,
  Text,
  Title,
} from "@mantine/core";
import { FaDiscord, FaExternalLinkAlt } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link, useParams } from "react-router-dom";

const pageSize = 5;

export default function UserSubmissions() {
  const { userId } = useParams();

  const { data, status, page, goBack, goForward, isPlaceholderData } =
    useUserSubmissionsQuery({
      userId,
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
        <Title>
          Submissions for{" "}
          <FaDiscord
            style={{ display: "inline", color: "var(--mantine-color-blue-5)" }}
          />{" "}
          <Title display={"inline"} c={"blue.5"}>
            {submissions[0].discordName}
          </Title>{" "}
          (
          <SiLeetcode
            style={{
              display: "inline",
              color: "var(--mantine-color-yellow-5)",
            }}
          />{" "}
          <Title display={"inline"} c={"yellow.5"}>
            {submissions[0].leetcodeUsername}
          </Title>
          )
        </Title>
        <Table verticalSpacing={"sm"}>
          <Table.Thead>
            <Table.Tr>
              <Table.Th />
              <Table.Th>Title</Table.Th>
              <Table.Th>Difficulty</Table.Th>
              <Table.Th>Acceptance Rate</Table.Th>
              <Table.Th>Pts</Table.Th>
              <Table.Th />
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
                if (acceptanceRate >= 30) {
                  return "red";
                }
                return undefined;
              })();

              return (
                <Table.Tr key={index}>
                  <Table.Td>{(page - 1) * pageSize + (index + 1)}</Table.Td>
                  <Table.Td>{submission.questionTitle}</Table.Td>
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
                    <Button
                      component={Link}
                      to={`/submission/s/${submission.id}`}
                      variant={"transparent"}
                    >
                      <FaExternalLinkAlt />
                    </Button>
                  </Table.Td>
                </Table.Tr>
              );
            })}
          </Table.Tbody>
        </Table>
        <Center>
          <Flex direction={"row"} gap={"sm"}>
            <Button disabled={page === 1} onClick={goBack}>
              Back
            </Button>
            <Button
              disabled={isPlaceholderData || !data.hasNextPage}
              onClick={() => {
                if (!isPlaceholderData && data.hasNextPage) {
                  goForward();
                }
              }}
            >
              Next
            </Button>
          </Flex>
        </Center>
      </Flex>
      <Footer />
    </>
  );
}
