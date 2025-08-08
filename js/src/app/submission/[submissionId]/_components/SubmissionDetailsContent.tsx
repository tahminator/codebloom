import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useSubmissionDetailsQuery } from "@/lib/api/queries/submissions";
import {
  Badge,
  Box,
  Button,
  Card,
  Center,
  Flex,
  Loader,
  Text,
  Title,
  Tooltip,
} from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { FiExternalLink } from "react-icons/fi";
import { SiLeetcode } from "react-icons/si";
import { Link, useNavigate } from "react-router-dom";
import SyntaxHighlighter from "react-syntax-highlighter";
import { gruvboxDark } from "react-syntax-highlighter/dist/esm/styles/hljs";

import classes from "./SubmissionDetailsContent.module.css";

export default function SubmissionDetailsContent({
  submissionId,
}: {
  submissionId?: string;
}) {
  const navigate = useNavigate();

  const { data, status } = useSubmissionDetailsQuery({ submissionId });

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  if (!data.success) {
    return <ToastWithRedirect message={data.message} to={"/dashboard"} />;
  }

  const {
    questionTitle,
    questionLink,
    pointsAwarded,
    questionDifficulty,
    description,
    acceptanceRate,
    discordName,
    nickname,
    leetcodeUsername,
    code,
    runtime,
    memory,
    language,
  } = data.payload;

  const badgeAcceptedColor = (() => {
    const ac = acceptanceRate * 100;
    if (ac >= 75) {
      return undefined;
    }
    if (ac >= 50) {
      return "yellow";
    }
    if (ac >= 30) {
      return "red";
    }
    return undefined;
  })();

  const badgeDifficultyColor = (() => {
    if (questionDifficulty === "Easy") {
      return undefined;
    }
    if (questionDifficulty === "Medium") {
      return "yellow";
    }
    if (questionDifficulty === "Hard") {
      return "red";
    }
    return undefined;
  })();

  return (
    <>
      <Header />
      <Box p={"lg"}>
        <Center>
          <Title mt="lg" mb="lg" order={3}>
            {questionTitle}
            <Button
              component={Link}
              to={questionLink}
              reloadDocument
              target="_blank"
              rel="noopener noreferrer"
              mx={"sm"}
              variant={"light"}
            >
              <FiExternalLink size={20} color="green" />
            </Button>
          </Title>
        </Center>

        <div style={{ padding: "0rem" }}>
          <Center>
            <Title order={3}>
              Solved by{" "}
              {nickname ?
                <Tooltip
                  label="This user is a verified member of the Patina Discord server."
                  color={"dark.4"}
                >
                  <IconCircleCheckFilled
                    style={{
                      display: "inline",
                      color: "var(--mantine-color-patina-4)",
                    }}
                    size={30}
                  />
                </Tooltip>
              : <FaDiscord
                  style={{
                    display: "inline",
                    color: "var(--mantine-color-blue-5)",
                  }}
                />
              }{" "}
              <Title
                display={"inline"}
                c={nickname ? "patina.4" : "blue.5"}
                order={3}
              >
                {nickname || discordName}
              </Title>{" "}
              <Link
                to={`https://leetcode.com/u/${leetcodeUsername}`}
                className="hover:underline"
              >
                (
                <SiLeetcode
                  style={{
                    display: "inline",
                    color: "var(--mantine-color-yellow-5)",
                  }}
                />{" "}
                <Title display={"inline"} c={"yellow.5"} order={3}>
                  {leetcodeUsername}
                </Title>
                ).
              </Link>
            </Title>
          </Center>
          <Center>
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Title fw={700} order={4}>
                Points:
              </Title>
              <Text size="lg">{pointsAwarded}</Text>
            </div>
          </Center>
          <Center>
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Title fw={700} order={4}>
                Difficulty:
              </Title>
              <Badge ta="center" color={badgeDifficultyColor}>
                {questionDifficulty}
              </Badge>
            </div>
          </Center>
          <Center>
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Title fw={700} order={4}>
                Aceptance Rate:
              </Title>
              <Badge ta={"center"} color={badgeAcceptedColor}>
                {Math.round(acceptanceRate * 100)}%
              </Badge>
            </div>
          </Center>
          <Center mt={"xs"}>
            <Button
              onClick={() => {
                navigate(-1);
              }}
            >
              ← Go back
            </Button>
          </Center>
          <Card shadow="xs" padding="lg" radius="lg" mt="xl">
            <div
              dangerouslySetInnerHTML={{ __html: description ?? "" }}
              style={{
                overflow: "auto",
                minWidth: 0,
              }}
              className={classes.description}
            />
          </Card>
          <Card shadow="xs" padding="lg" radius="lg" mt="xl">
            <Flex direction={"column"} gap={"md"} align={"center"}>
              <Title order={3}>
                {(language ?? "")[0]?.toUpperCase() + (language ?? "").slice(1)}
              </Title>
              <Text>Runtime: {runtime ?? ""}</Text>
              <Text>Memory: {memory ?? ""}</Text>
            </Flex>
            <SyntaxHighlighter
              style={gruvboxDark}
              customStyle={{
                overflow: "auto",
                minWidth: 0,
                borderRadius: "8px",
              }}
              language={
                (language ?? "") === "python3" ? "python" : (language ?? "")
              }
            >
              {code ?? ""}
            </SyntaxHighlighter>
          </Card>
        </div>
      </Box>
      <Footer />
    </>
  );
}
