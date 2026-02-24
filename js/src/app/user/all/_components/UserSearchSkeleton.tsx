import { theme } from "@/lib/theme";
import { Box, Divider, Flex, Skeleton, Stack } from "@mantine/core";

function UserSearchSkeletonItem({ index }: { index: number }) {
  const hasLeetcode = index % 2 === 0;
  const hasNickname = index % 3 !== 2;
  const hasTags = index % 4 === 0;

  return (
    <Box
      p="md"
      style={{
        borderTop: index > 0 ? `1px solid ${theme.colors.dark[5]}` : undefined,
      }}
    >
      <Flex direction="column" gap={6}>
        <Flex direction="row" gap="md" align="center" wrap="wrap">
          <Flex align="center" gap={8}>
            <Skeleton circle width={18} height={18} />
            <Skeleton height={16} width={80 + (index % 3) * 20} radius="sm" />
          </Flex>
          {hasLeetcode && (
            <Flex align="center" gap={8}>
              <Skeleton circle width={18} height={18} />
              <Skeleton height={16} width={60 + (index % 4) * 15} radius="sm" />
            </Flex>
          )}
        </Flex>
        {(hasNickname || hasTags) && (
          <Flex align="center" gap={6}>
            {hasNickname && (
              <Flex align="center" gap={6}>
                <Skeleton circle width={16} height={16} />
                <Skeleton
                  height={14}
                  width={70 + (index % 5) * 12}
                  radius="sm"
                />
              </Flex>
            )}
            {hasNickname && hasTags && (
              <Divider orientation="vertical" h={18} />
            )}
            {hasTags && (
              <Flex align="center" gap={4}>
                <Skeleton circle width={16} height={16} />
                <Skeleton circle width={16} height={16} />
              </Flex>
            )}
          </Flex>
        )}
      </Flex>
    </Box>
  );
}

export default function UserSearchSkeleton() {
  return (
    <Stack gap={0} data-testid="user-search-skeleton">
      {Array.from({ length: 5 }).map((_, i) => (
        <UserSearchSkeletonItem key={i} index={i} />
      ))}
    </Stack>
  );
}
