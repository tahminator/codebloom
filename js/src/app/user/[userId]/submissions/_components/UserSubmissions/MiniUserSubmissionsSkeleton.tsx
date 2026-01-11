import { Skeleton, Stack, Group, Paper, ScrollArea } from "@mantine/core";

export default function MiniUserSubmissionsSkeleton() {
  return (
    <>
      <ScrollArea maw="100%" miw="66%" p="xs">
        <Stack gap="md" my="sm">
          {Array(5)
            .fill(0)
            .map((_, index) => (
              <Paper
                key={index}
                bg="rgba(255, 255, 255, 0.02)"
                radius={8}
                p="md"
              >
                <Group justify="space-between" align="flex-start" mb="sm">
                  <Skeleton visible width="30%" height="55px" />
                  <Skeleton visible width="40px" height="12px" />
                </Group>
                <Group justify="flex-end">
                  <Skeleton visible width="40px" height="12px" />
                </Group>
              </Paper>
            ))}
        </Stack>
      </ScrollArea>
    </>
  );
}
