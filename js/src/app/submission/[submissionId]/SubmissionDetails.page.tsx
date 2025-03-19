import SubmissionDetailsContent from "@/app/submission/[submissionId]/_components/SubmissionDetailsContent";
import { useParams } from "react-router";

export default function SubmissionDetailsPage() {
  const { submissionId } = useParams();

  return <SubmissionDetailsContent submissionId={submissionId} />;
}
