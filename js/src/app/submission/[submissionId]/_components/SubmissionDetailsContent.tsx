import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useSubmissionDetailsQuery } from "@/lib/api/queries/submissions";
import { capitalize } from "@/lib/helper/capitalize";
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
} from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { FiExternalLink } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import SyntaxHighlighter from "react-syntax-highlighter";
import { gruvboxDark } from "react-syntax-highlighter/dist/esm/styles/hljs";

import classes from "./SubmissionDetailsContent.module.css";

export default function SubmissionDetailsContent({
  submissionId,
}: {
  submissionId: string;
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
    code,
    runtime,
    memory,
    language,
    userId,
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

  const parsedLanguage = (() => {
    if (!language) return undefined;
    return language === "python3" ? "python" : language;
  })();

  return (
    <>
      <DocumentTitle title={`CodeBloom - ${questionTitle}`} />
      <DocumentDescription
        description={`CodeBloom - View ${discordName}'s solution for ${questionTitle}`}
      />
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
              <FaDiscord
                style={{
                  display: "inline",
                  color: "var(--mantine-color-blue-5)",
                }}
              />{" "}
              <Title display={"inline"} c={"blue.5"} order={3}>
                {discordName}
              </Title>
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
                navigate(`/user/${userId}`);
              }}
            >
              ← Go back to profile
            </Button>
          </Center>
          <Card shadow="xs" padding="lg" radius="lg" mt="xl">
            <div
              dangerouslySetInnerHTML={{
                __html: description ?? "No available description found.",
              }}
              style={{
                overflow: "auto",
                minWidth: 0,
              }}
              className={classes.description}
            />
          </Card>
          <Card shadow="xs" padding="lg" radius="lg" mt="xl">
            <Flex direction={"column"} gap={"md"} align={"center"}>
              <Title order={3}>{capitalize(language ?? "Unknown")}</Title>
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
              language={parsedLanguage}
            >
              {code ?? "No code available."}
            </SyntaxHighlighter>
          </Card>
        </div>
      </Box>
      <Footer />
    </>
  );
}
