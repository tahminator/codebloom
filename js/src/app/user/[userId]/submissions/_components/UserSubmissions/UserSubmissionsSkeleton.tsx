import {
  Center,
  Skeleton,
  Stack,
  Group,
  Paper,
  Flex,
  Box,
} from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";

export default function UserSubmissionsSkeleton() {
  const isMobile = useMediaQuery("(max-width: 768px)");

  return (
    <>
      <Box
        mt={10}
        pos="relative"
        px={isMobile ? "xs" : undefined}
        w={"100%"}
        maw={isMobile ? undefined : 925}
        p={isMobile ? undefined : "xs"}
      >
        {!isMobile && (
          <div>
            <Skeleton
              visible
              width="80px"
              height="36px"
              ml="auto"
              display="block"
            />
            <Center mb="md">
              <Skeleton visible w="100%" h={38} mt={8} />
            </Center>
          </div>
        )}
        {isMobile && (
          <Group justify="space-between" align="flex-end" gap="xs" mb="sm">
            <Box flex={1} miw={0}>
              <Skeleton visible w="100%" h="38px" />
            </Box>
            <Skeleton visible w="80px" h="38px" />
          </Group>
        )}
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
                  <Skeleton visible width="20%" height="20px" />
                  <Skeleton visible width="40px" height="10px" />
                </Group>
                <Group align="flex-start" mb="sm">
                  <Skeleton visible width="50px" height="15px" />
                  <Skeleton visible width="35px" height="15px" />
                </Group>
                <Group align="flex-start">
                  <Skeleton visible width="50px" height="15px" />
                  <Skeleton visible width="55px" height="15px" />
                  <Skeleton visible width="45px" height="15px" />
                </Group>
                <Group justify="flex-end">
                  <Skeleton visible width="40px" height="12px" />
                </Group>
              </Paper>
            ))}
        </Stack>
        <Flex p="xl" justify="center">
          {[...Array(3)].map((_, i) => (
            <Skeleton key={i} height={30} width={30} mr={8} />
          ))}
        </Flex>
      </Box>
    </>
  );
}
