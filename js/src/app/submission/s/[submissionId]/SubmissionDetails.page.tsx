import { useSubmissionDetailsQuery } from "@/app/submission/s/[submissionId]/hooks";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import {
  Badge,
  Box,
  Button,
  Card,
  Center,
  Loader,
  Text,
  Title,
} from "@mantine/core";
import { FiExternalLink } from "react-icons/fi";
import { Link, useNavigate, useParams } from "react-router-dom";
import classes from "./SubmissionDetail.module.css";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function SubmissionDetails() {
  const navigate = useNavigate();

  const { submissionId } = useParams();

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

  if (!data.data) {
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
    leetcodeUsername,
  } = data.data;

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
              <FaDiscord
                style={{
                  display: "inline",
                  color: "var(--mantine-color-blue-5)",
                }}
              />{" "}
              <Title display={"inline"} c={"blue.5"} order={3}>
                {discordName}
              </Title>{" "}
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
              ← Back to dashboard
            </Button>
          </Center>
          <Card shadow="xs" padding="lg" radius="lg" mt="xl">
            <div
              dangerouslySetInnerHTML={{ __html: description }}
              style={{
                overflow: "auto",
                minWidth: 0,
              }}
              className={classes.description}
            />
          </Card>
        </div>
      </Box>
      <Footer />
    </>
  );
}
