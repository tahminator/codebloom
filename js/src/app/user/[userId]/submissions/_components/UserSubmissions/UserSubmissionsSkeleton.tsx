import {
  Center,
  Skeleton,
  Stack,
  Group,
  Paper,
  Flex,
  ScrollArea,
} from "@mantine/core";

export default function UserSubmissionsSkeleton() {
  return (
    <>
      <ScrollArea maw="100%" miw="66%" p="xs">
        <Skeleton
          visible
          width="80px"
          height="36px"
          ml="auto"
          display="block"
        ></Skeleton>
        <Center mb="md">
          <Skeleton visible w="100%" h={38} mt={8} />
        </Center>
        <Stack gap="md" my="sm">
          {Array(2)
            .fill(0)
            .map((_, index) => (
              <Paper
                key={index}
                bg="rgba(255, 255, 255, 0.02)"
                radius={8}
                p="md"
              >
                <Group justify="space-between" align="flex-start" mb="sm">
                  <Skeleton visible width="40%" height="70px" />
                  <Skeleton visible width="40px" height="12px" />
                </Group>
                <Group justify="flex-end">
                  <Skeleton visible width="40px" height="12px" />
                </Group>
              </Paper>
            ))}
        </Stack>
        <Flex p="xl" display="flex" justify="center">
          {[...Array(3)].map((_, i) => (
            <Skeleton key={i} height={30} width={30} mr={8} />
          ))}
        </Flex>
      </ScrollArea>
    </>
  );
}
