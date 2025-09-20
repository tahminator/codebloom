import { Flex, Skeleton, Stack } from "@mantine/core";

export default function UserProfileHeaderSkeleton() {
  return (
    <>
      <Flex direction={"row"} wrap={"wrap"}>
        <Stack justify="center">
          <Skeleton height={30} width={190}/>
          <Skeleton height={30} width={190}/>
        </Stack>
      </Flex>
    </>
  );
}
