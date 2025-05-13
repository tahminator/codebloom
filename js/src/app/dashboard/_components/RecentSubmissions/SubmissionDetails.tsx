import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import { useSubmissionDetailsQuery } from "@/lib/api/queries/submissions";
import { Center, Loader, Title, Text, Card } from "@mantine/core";
import { FiExternalLink } from "react-icons/fi";
import { useParams } from "react-router-dom";

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

  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const {
    questionTitle,
    questionLink,
    pointsAwarded,
    questionDifficulty,
    description,
    acceptanceRate,
  } = data.payload;

  return (
    <div>
      <Header />
      <Center>
        <Title mt="lg" mb="lg" order={3}>
          Question Details!
        </Title>
      </Center>
      <div style={{ padding: "2.5rem" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <Title fw={700} order={4}>
            Question Title:
          </Title>
          <Text size="lg">{questionTitle}</Text>
          <a href={questionLink} target="_blank" rel="noopener noreferrer">
            <FiExternalLink size={20} color="green" />
          </a>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <Title fw={700} order={4}>
            Points:
          </Title>
          <Text size="lg">{pointsAwarded}</Text>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <Title fw={700} order={4}>
            Difficulty:
          </Title>
          <Text size="lg">{questionDifficulty}</Text>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <Title fw={700} order={4}>
            Aceptance Rate:
          </Title>
          <Text size="lg">{acceptanceRate * 100}%</Text>
        </div>
        <Card shadow="xs" padding="lg" radius="lg" mt="xl">
          <div dangerouslySetInnerHTML={{ __html: description }}></div>
        </Card>
      </div>
      <Footer />
    </div>
  );
}
