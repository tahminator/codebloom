import { Flex, Skeleton, Stack } from "@mantine/core";

export default function UserProfileHeaderSkeleton() {
  return (
    <>
      <Flex direction={"row"} wrap={"wrap"}>
        <Stack justify="center">
          <Skeleton height={70} width={190} />
        </Stack>
      </Flex>
    </>
  );
}
