import { useSubmissionDetailsQuery } from "@/app/submission/[id]/hooks";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Button, Card, Center, Loader, Text, Title } from "@mantine/core";
import { FiExternalLink } from "react-icons/fi";
import { Link, useParams } from "react-router-dom";
import classes from "./SubmissionDetail.module.css";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function SubmissionDetails() {
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

  return (
    <>
      <Header />
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
            Solved by <FaDiscord style={{ display: "inline" }} /> {discordName}{" "}
            (
            <SiLeetcode style={{ display: "inline" }} /> {leetcodeUsername}).
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
            <Text size="lg">{questionDifficulty}</Text>
          </div>
        </Center>
        <Center>
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <Title fw={700} order={4}>
              Aceptance Rate:
            </Title>
            <Text size="lg">{Math.round(acceptanceRate * 100)}%</Text>
          </div>
        </Center>
        <Center mt={"xs"}>
          <Button component={Link} to={"/dashboard"}>
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
      <Footer />
    </>
  );
}
