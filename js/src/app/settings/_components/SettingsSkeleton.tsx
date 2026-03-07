import CodebloomCard from "@/components/ui/CodebloomCard";
import { Box, Center, Flex, Skeleton, Stack } from "@mantine/core";

export default function SettingsSkeleton() {
  return (
    <Box mih="90vh" p="lg">
      <Box>
        <Center>
          <Skeleton
            w="8rem"
            h="2.5rem"
            my="md"
            data-testid="settings-skeleton-title"
          />
        </Center>
        <Stack gap="xl">
          <CodebloomCard data-testid="settings-skeleton-verify-school-card">
            <Box m="md">
              <Skeleton
                w="12rem"
                h="3rem"
                mb="xl"
                ml="md"
                data-testid="settings-skeleton-verify-school-title"
              />
              <Skeleton
                w="40%"
                h="1.25rem"
                mb="md"
                ml="md"
                data-testid="settings-skeleton-verify-school-description-1"
              />
              <Skeleton
                w="9rem"
                h="1.25rem"
                mb="md"
                ml="md"
                data-testid="settings-skeleton-verify-school-description-2"
              />
              <Flex direction="column" gap="xs" mb="lg" ml="md">
                {Array(9)
                  .fill(0)
                  .map((_, index) => (
                    <Skeleton
                      key={index}
                      w="12rem"
                      h="1.5rem"
                      data-testid="settings-skeleton-verify-school-list"
                    />
                  ))}
              </Flex>
              <Skeleton
                w="6rem"
                h="2.25rem"
                ml="md"
                radius="md"
                data-testid="settings-skeleton-verify-now-button"
              />
            </Box>
          </CodebloomCard>
          <CodebloomCard data-testid="settings-skeleton-change-profile-card">
            <Box m="md">
              <Skeleton
                w="22rem"
                h="2.5rem"
                mb="lg"
                ml="md"
                data-testid="settings-skeleton-change-profile-title"
              />
              <Skeleton
                w="35%"
                h="1.25rem"
                mb="md"
                ml="md"
                data-testid="settings-skeleton-change-profile-description-1"
              />
              <Skeleton
                w="45%"
                h="1.25rem"
                mb="md"
                ml="md"
                data-testid="settings-skeleton-change-profile-description-2"
              />
              <Skeleton
                w="50%"
                h="1.25rem"
                ml="md"
                data-testid="settings-skeleton-change-profile-description-3"
              />
            </Box>
          </CodebloomCard>
          <CodebloomCard data-testid="settings-skeleton-log-out-card">
            <Box m="md">
              <Skeleton
                w="20rem"
                h="2.5rem"
                mb="lg"
                ml="md"
                data-testid="settings-skeleton-log-out-title"
              />
              <Skeleton
                w="55%"
                h="1.25rem"
                mb="md"
                ml="md"
                data-testid="settings-skeleton-log-out-description"
              />
              <Skeleton
                w="12rem"
                h="2rem"
                ml="md"
                radius="md"
                data-testid="settings-skeleton-log-out-all-sessions-button"
              />
            </Box>
          </CodebloomCard>
        </Stack>
      </Box>
    </Box>
  );
}
