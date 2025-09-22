import {
  langNameKey,
  langNameToIcon,
} from "@/components/ui/langname-to-icon/LangNameToIcon";
import Toast from "@/components/ui/toast/Toast";
import { useUserSubmissionsQuery } from "@/lib/api/queries/user";
import { timeDiff } from "@/lib/timeDiff";
import {
  Badge,
  Box,
  Overlay,
  Table,
  Text,
} from "@mantine/core";
import { Link } from "react-router-dom";

import MiniUserSubmissionsSkeleton from "./MiniUserSubmissionsSkeleton";

export default function MiniUserSubmissions({ userId }: { userId?: string }) {
  const { data, status, isPlaceholderData } = useUserSubmissionsQuery({
    userId,
    tieToUrl: true,
    pageSize: 5,
  });

  if (status === "pending") {
    return (
      <>
        <MiniUserSubmissionsSkeleton />
      </>
    );
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong when trying to fetch user's submissions. Please try again later." />
    );
  }

  if (!data.success) {
    return <>{data.message}</>;
  }

  const pageData = data.payload;

  return (
    <>
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
            {pageData.items.length == 0 && (
              <Table.Tr>
                <Table.Td colSpan={100} bg="gray.9">
                  <Text fw={500} ta="center">
                    Nothing found
                  </Text>
                </Table.Td>
              </Table.Tr>
            )}
            {pageData.items.map((submission, index) => {
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
                      to={`/submission/${submission.id}`}
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
            {pageData.items.length < pageData.pageSize &&
              Array(pageData.pageSize - pageData.items.length)
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
    </>
  );
}
