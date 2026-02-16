import { Box, Card, Center, Flex, Skeleton, Stack } from "@mantine/core";

export default function SettingsSkeleton() {
  return (
    <Box mih="90vh" p="lg">
      <Box>
        <Center>
          <Skeleton w="8rem" h="2.5rem" my="md" />
        </Center>
        <Stack gap="xl">
          <Card withBorder padding="md" radius="md">
            <Box m="md">
              <Skeleton w="12rem" h="3rem" mb="xl" ml="md" />
              <Skeleton w="40%" h="1.25rem" mb="md" ml="md" />
              <Skeleton w="9rem" h="1.25rem" mb="md" ml="md" />
              <Flex direction="column" gap="xs" mb="lg" ml="md">
                {Array(9)
                  .fill(0)
                  .map((_, index) => (
                    <Skeleton key={index} w="12rem" h="1.5rem" />
                  ))}
              </Flex>
              <Skeleton w="6rem" h="2.25rem" ml="md" radius="md" />
            </Box>
          </Card>
          <Card withBorder padding="md" radius="md">
            <Box m="md">
              <Skeleton w="22rem" h="2.5rem" mb="lg" ml="md" />
              <Skeleton w="35%" h="1.25rem" mb="md" ml="md" />
              <Skeleton w="45%" h="1.25rem" mb="md" ml="md" />
              <Skeleton w="50%" h="1.25rem" ml="md" />
            </Box>
          </Card>
          <Card withBorder padding="md" radius="md">
            <Box m="md">
              <Skeleton w="20rem" h="2.5rem" mb="lg" ml="md" />
              <Skeleton w="55%" h="1.25rem" mb="md" ml="md" />
              <Skeleton w="12rem" h="2rem" ml="md" radius="md" />
            </Box>
          </Card>
        </Stack>
      </Box>
    </Box>
  );
}
