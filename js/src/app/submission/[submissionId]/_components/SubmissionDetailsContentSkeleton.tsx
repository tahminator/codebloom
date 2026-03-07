import CodebloomCard from "@/components/ui/CodebloomCard";
import { Box, Center, Flex, Skeleton } from "@mantine/core";

export default function SubmissionDetailsContentSkeleton() {
  return (
    <Box p="lg">
      <Center>
        <Flex align="center" gap="sm" mt="lg" mb="lg">
          <Skeleton
            w="20rem"
            h="3rem"
            data-testid="submission-skeleton-question-title"
          />
          <Skeleton
            w="4rem"
            h="3rem"
            radius="md"
            data-testid="submission-skeleton-question-link-button"
          />
        </Flex>
      </Center>
      <Box p={0}>
        <Center>
          <Flex align="center" gap="xs">
            <Skeleton
              w="24rem"
              h="2.25rem"
              data-testid="submission-skeleton-question-solved-by"
            />
          </Flex>
        </Center>
        <Center mt="xs">
          <Skeleton
            w="8rem"
            h="1.75rem"
            data-testid="submission-skeleton-question-points"
          />
        </Center>
        <Center mt="xs">
          <Skeleton
            w="10rem"
            h="1.75rem"
            data-testid="submission-skeleton-question-difficulty"
          />
        </Center>
        <Center mt="xs">
          <Skeleton
            w="14rem"
            h="1.75rem"
            data-testid="submission-skeleton-question-acceptance-rate"
          />
        </Center>
        <Center mt="xs">
          <Skeleton
            w="10rem"
            h="2.25rem"
            radius="md"
            data-testid="submission-skeleton-user-profile-button"
          />
        </Center>
        <CodebloomCard
          shadow="xs"
          padding="lg"
          radius="lg"
          mt="xl"
          data-testid="submission-skeleton-question-details"
        >
          <Skeleton w="70%" h="1.25rem" mb="md" />
          <Skeleton w="6rem" h="1.5rem" mb="sm" />
          <Skeleton w="100%" h="10rem" mb="lg" />
          <Skeleton w="6rem" h="1.5rem" mb="sm" />
          <Skeleton w="100%" h="6rem" mb="lg" />
          <Skeleton w="6rem" h="1.5rem" mb="sm" />
          <Skeleton w="100%" h="4rem" mb="lg" />
          <Skeleton w="10%" h="1.5rem" mb="xs" />
          <Skeleton w="10%" h="1.5rem" />
        </CodebloomCard>
        <CodebloomCard
          shadow="xs"
          padding="lg"
          radius="lg"
          mt="xl"
          data-testid="submission-skeleton-user-solution"
        >
          <Flex direction="column" gap="md" align="center" mb="lg">
            <Skeleton w="10rem" h="3rem" />
            <Skeleton w="6rem" h="1.5rem" />
            <Skeleton w="6rem" h="1.5rem" />
          </Flex>
          <Skeleton w="100%" h="20rem" />
        </CodebloomCard>
      </Box>
    </Box>
  );
}
